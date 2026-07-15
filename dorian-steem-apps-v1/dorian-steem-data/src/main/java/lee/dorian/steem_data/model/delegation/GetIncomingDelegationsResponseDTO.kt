package lee.dorian.steem_data.model.delegation

import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_domain.ext.removeSubstring
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.util.Converter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class GetIncomingDelegationsResponseDTO(
    val code: Int?,
    val result: IncomingDelegationsResultDTO?
)

data class IncomingDelegationsResultDTO(
    val cols: IncomingDelegationsColsDTO?,
    val rows: List<List<Any>>?
) {
    fun toVestingDelegations(dgp: GetDynamicGlobalPropertiesDTO): List<VestingDelegation> {
        val timeIdx = cols?.time ?: 0
        val fromIdx = cols?.from ?: 1
        val vestsIdx = cols?.vests ?: 3
        val totalVestingShares = dgp.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val totalVestingFundSteem = dgp.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f

        return rows?.mapNotNull { row ->
            if (row.size < 4) return@mapNotNull null
            val timestamp = (row[timeIdx] as? Double)?.toLong() ?: return@mapNotNull null
            val from = row[fromIdx] as? String ?: return@mapNotNull null
            val vests = (row[vestsIdx] as? Double)?.toFloat() ?: return@mapNotNull null
            val sp = Converter.toSteemPowerFromVest(vests, totalVestingShares, totalVestingFundSteem)
            VestingDelegation(
                delegator = from,
                steemPower = String.format("%.3f SP", sp),
                minDelegationTime = timestamp.toUtcDateString()
            )
        } ?: emptyList()
    }

    private fun Long.toUtcDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(this * 1000L))
    }
}

data class IncomingDelegationsColsDTO(
    val time: Int?,
    val from: Int?,
    val to: Int?,
    val vests: Int?
)
