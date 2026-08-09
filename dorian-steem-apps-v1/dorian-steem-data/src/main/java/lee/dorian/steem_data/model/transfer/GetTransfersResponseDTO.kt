package lee.dorian.steem_data.model.transfer

import lee.dorian.steem_domain.model.Transfer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetTransfersResponseDTO(
    val code: Int?,
    val error: String?,
    val result: TransfersResultDTO?
)

data class TransfersResultDTO(
    val cols: TransfersColsDTO?,
    val rows: List<List<Any>>?
) {
    fun toTransferList(): List<TransferDTO> {
        val timeIdx = cols?.time ?: DEFAULT_TIME_INDEX
        val fromIdx = cols?.from ?: DEFAULT_FROM_INDEX
        val toIdx = cols?.to ?: DEFAULT_TO_INDEX
        val amountIdx = cols?.amount ?: DEFAULT_AMOUNT_INDEX
        val unitIdx = cols?.unit ?: DEFAULT_UNIT_INDEX
        val memoIdx = cols?.memo ?: DEFAULT_MEMO_INDEX

        return rows?.mapNotNull { row ->
            TransferDTO(
                time = (row.getOrNull(timeIdx) as? Double)?.toLong() ?: return@mapNotNull null,
                from = row.getOrNull(fromIdx) as? String ?: return@mapNotNull null,
                to = row.getOrNull(toIdx) as? String ?: return@mapNotNull null,
                amount = (row.getOrNull(amountIdx) as? Double)?.toFloat() ?: return@mapNotNull null,
                unit = row.getOrNull(unitIdx) as? String ?: "",
                memo = row.getOrNull(memoIdx) as? String ?: ""
            )
        } ?: listOf()
    }

    fun toTransfers(): List<Transfer> {
        return toTransferList().map { it.toTransfer() }
    }

    companion object {
        const val DEFAULT_TIME_INDEX = 0
        const val DEFAULT_FROM_INDEX = 1
        const val DEFAULT_TO_INDEX = 2
        const val DEFAULT_AMOUNT_INDEX = 3
        const val DEFAULT_UNIT_INDEX = 4
        const val DEFAULT_MEMO_INDEX = 5
    }
}

data class TransfersColsDTO(
    val time: Int?,
    val from: Int?,
    val to: Int?,
    val amount: Int?,
    val unit: Int?,
    val memo: Int?
)

// One row of TransfersResultDTO.rows, mapped to the order declared in "cols".
data class TransferDTO(
    val time: Long,       // Unix timestamp in seconds
    val from: String,
    val to: String,
    val amount: Float,
    val unit: String,     // STEEM, SBD or VESTS
    val memo: String
) {
    fun toTransfer(): Transfer {
        return Transfer(
            time = time.toLocalTimeString(),
            from = from,
            to = to,
            amount = "${String.format(Locale.US, amountFormatOfUnit(), amount)} $unit",
            memo = memo
        )
    }

    // VESTS keeps 6 decimal places on the Steem blockchain, while STEEM and SBD keep 3.
    private fun amountFormatOfUnit(): String {
        return when (unit) {
            UNIT_VESTS -> "%.6f"
            else -> "%.3f"
        }
    }

    private fun Long.toLocalTimeString(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        return sdf.format(Date(this * 1000L))
    }

    companion object {
        const val UNIT_VESTS = "VESTS"
        const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    }
}

// Example of a successful response.
// {
//     "code": 0,
//     "result": {
//         "cols": {
//             "time": 0,
//             "from": 1,
//             "to": 2,
//             "amount": 3,
//             "unit": 4,
//             "memo": 5
//         },
//         "rows": [
//             [
//                 1786244505,
//                 "jsup",
//                 "dorian-lee",
//                 18.868,
//                 "STEEM",
//                 "curation reward from jsup: 18.868 STEEM"
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
