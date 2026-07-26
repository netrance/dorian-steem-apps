package lee.dorian.steem_data.model.delegation

import lee.dorian.dorian_ktx.removeSubstring
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.util.Converter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class GetExpiringDelegationsResponseDTO(
    val code: Int?,
    val result: ExpiringDelegationsResultDTO?
)

data class ExpiringDelegationsResultDTO(
    val cols: ExpiringDelegationsColsDTO?,
    val rows: List<List<Any>>?
) {
    fun toExpiringVestingDelegations(dgp: GetDynamicGlobalPropertiesDTO): List<ExpiringVestingDelegation> {
        val expirationIdx = cols?.expiration ?: 1
        val toIdx = cols?.to ?: 3
        val vestsIdx = cols?.vests ?: 4
        val totalVestingShares = dgp.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val totalVestingFundSteem = dgp.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f

        return rows?.mapNotNull { row ->
            if (row.size < 5) return@mapNotNull null
            val timestamp = (row[expirationIdx] as? Double)?.toLong() ?: return@mapNotNull null
            val to = row[toIdx] as? String ?: return@mapNotNull null
            val vests = (row[vestsIdx] as? Double)?.toFloat() ?: return@mapNotNull null
            val sp = Converter.toSteemPowerFromVest(vests, totalVestingShares, totalVestingFundSteem)
            ExpiringVestingDelegation(
                delegatee = to,
                steemPower = String.format("%.3f SP", sp),
                expiration = timestamp.toUtcDateString()
            )
        } ?: emptyList()
    }

    private fun Long.toUtcDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(this * 1000L))
    }
}

data class ExpiringDelegationsColsDTO(
    val time: Int?,
    val expiration: Int?,
    val from: Int?,
    val to: Int?,
    val vests: Int?
)
