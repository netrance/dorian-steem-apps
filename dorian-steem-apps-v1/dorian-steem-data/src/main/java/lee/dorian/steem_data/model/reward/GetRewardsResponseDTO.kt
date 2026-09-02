package lee.dorian.steem_data.model.reward

import lee.dorian.dorian_ktx.removeSubstring
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.model.RewardType
import lee.dorian.steem_domain.util.Converter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetRewardsResponseDTO(
    val code: Int?,
    val error: String?,
    val result: RewardsResultDTO?
)

// rewards_api declares a different set of columns for every op, so the rows have to be
// mapped with the column set of the op they were requested with.
data class RewardsResultDTO(
    val cols: RewardsColsDTO?,
    val rows: List<List<Any>>?
) {
    // The rows of the "author_reward" op: [time, author, permlink, sbd, steem, vests].
    fun toAuthorRewardList(): List<RewardDTO> {
        val timeIdx = cols?.time ?: AUTHOR_TIME_INDEX
        val authorIdx = cols?.author ?: AUTHOR_AUTHOR_INDEX
        val permlinkIdx = cols?.permlink ?: AUTHOR_PERMLINK_INDEX
        val sbdIdx = cols?.sbd ?: AUTHOR_SBD_INDEX
        val steemIdx = cols?.steem ?: AUTHOR_STEEM_INDEX
        val vestsIdx = cols?.vests ?: AUTHOR_VESTS_INDEX

        return rows?.mapNotNull { row ->
            RewardDTO(
                time = (row.getOrNull(timeIdx) as? Double)?.toLong() ?: return@mapNotNull null,
                type = RewardType.AUTHOR,
                sbd = (row.getOrNull(sbdIdx) as? Double)?.toFloat() ?: 0f,
                steem = (row.getOrNull(steemIdx) as? Double)?.toFloat() ?: 0f,
                vests = (row.getOrNull(vestsIdx) as? Double)?.toFloat() ?: 0f,
                author = row.getOrNull(authorIdx) as? String ?: "",
                permlink = row.getOrNull(permlinkIdx) as? String ?: ""
            )
        } ?: listOf()
    }

    // The rows of the "curation_reward" op: [time, vests, author, permlink].
    fun toCurationRewardList(): List<RewardDTO> {
        val timeIdx = cols?.time ?: CURATION_TIME_INDEX
        val vestsIdx = cols?.vests ?: CURATION_VESTS_INDEX
        val authorIdx = cols?.author ?: CURATION_AUTHOR_INDEX
        val permlinkIdx = cols?.permlink ?: CURATION_PERMLINK_INDEX

        return rows?.mapNotNull { row ->
            RewardDTO(
                time = (row.getOrNull(timeIdx) as? Double)?.toLong() ?: return@mapNotNull null,
                type = RewardType.CURATION,
                sbd = 0f,
                steem = 0f,
                vests = (row.getOrNull(vestsIdx) as? Double)?.toFloat() ?: 0f,
                author = row.getOrNull(authorIdx) as? String ?: "",
                permlink = row.getOrNull(permlinkIdx) as? String ?: ""
            )
        } ?: listOf()
    }

    fun toAuthorRewards(dgp: GetDynamicGlobalPropertiesDTO): List<Reward> {
        return toAuthorRewardList().map { it.toReward(dgp) }
    }

    fun toCurationRewards(dgp: GetDynamicGlobalPropertiesDTO): List<Reward> {
        return toCurationRewardList().map { it.toReward(dgp) }
    }

    companion object {
        // The default column indices of the "author_reward" op.
        const val AUTHOR_TIME_INDEX = 0
        const val AUTHOR_AUTHOR_INDEX = 1
        const val AUTHOR_PERMLINK_INDEX = 2
        const val AUTHOR_SBD_INDEX = 3
        const val AUTHOR_STEEM_INDEX = 4
        const val AUTHOR_VESTS_INDEX = 5

        // The default column indices of the "curation_reward" op.
        const val CURATION_TIME_INDEX = 0
        const val CURATION_VESTS_INDEX = 1
        const val CURATION_AUTHOR_INDEX = 2
        const val CURATION_PERMLINK_INDEX = 3
    }
}

// The union of the columns declared by the ops this app reads. Every op declares only
// its own columns, so the ones it does not pay out are missing from the response.
data class RewardsColsDTO(
    val time: Int?,
    val author: Int?,
    val permlink: Int?,
    val sbd: Int?,
    val steem: Int?,
    val vests: Int?
)

// One row of RewardsResultDTO.rows, mapped to the order declared in "cols".
data class RewardDTO(
    val time: Long,        // Unix timestamp in seconds
    val type: RewardType,
    val sbd: Float,
    val steem: Float,
    val vests: Float,      // Paid out as Steem Power, so it is converted with the global properties
    val author: String,
    val permlink: String
) {
    fun toReward(dgp: GetDynamicGlobalPropertiesDTO): Reward {
        val totalVestingShares = dgp.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val totalVestingFundSteem = dgp.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f
        val steemPower = when (totalVestingShares) {
            0f -> 0f
            else -> Converter.toSteemPowerFromVest(vests, totalVestingShares, totalVestingFundSteem)
        }

        return Reward(
            time = time.toLocalTimeString(),
            type = type,
            amount = amountString(steemPower),
            author = author,
            permlink = permlink
        )
    }

    // A single reward can be paid out in more than one unit, so only the units that were
    // actually paid out are listed. (e.g. "0.323 SBD, 78.416 SP")
    private fun amountString(steemPower: Float): String {
        val paidUnits = listOf(sbd to UNIT_SBD, steem to UNIT_STEEM, steemPower to UNIT_STEEM_POWER)
            .filter { (amount, _) -> amount != 0f }
            .map { (amount, unit) -> String.format(Locale.US, "%.3f $unit", amount) }

        return when {
            paidUnits.isEmpty() -> String.format(Locale.US, "%.3f $UNIT_STEEM_POWER", 0f)
            else -> paidUnits.joinToString(", ")
        }
    }

    private fun Long.toLocalTimeString(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        return sdf.format(Date(this * 1000L))
    }

    companion object {
        const val UNIT_SBD = "SBD"
        const val UNIT_STEEM = "STEEM"
        const val UNIT_STEEM_POWER = "SP"
        const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    }
}

// Example of a successful "author_reward" response.
// {
//     "code": 0,
//     "result": {
//         "cols": {
//             "time": 0,
//             "author": 1,
//             "permlink": 2,
//             "sbd": 3,
//             "steem": 4,
//             "vests": 5
//         },
//         "rows": [
//             [
//                 1517879550,
//                 "dorian-lee",
//                 "first-post-f2dfdda06c3fb",
//                 0.323,
//                 0,
//                 159.549733
//             ]
//         ]
//     }
// }
//
// Example of a successful "curation_reward" response.
// {
//     "code": 0,
//     "result": {
//         "cols": {
//             "time": 0,
//             "vests": 1,
//             "author": 2,
//             "permlink": 3
//         },
//         "rows": [
//             [
//                 1518735099,
//                 4.08892,
//                 "steem.power",
//                 "more-jackets"
//             ]
//         ]
//     }
// }
//
// Example of a failed response. (e.g. non-existent account)
// {
//     "code": -1,
//     "error": "Account id for 'invalid10293845' does not exist"
// }
