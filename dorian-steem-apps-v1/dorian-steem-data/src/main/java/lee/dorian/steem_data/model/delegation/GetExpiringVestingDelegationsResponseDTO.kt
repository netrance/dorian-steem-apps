package lee.dorian.steem_data.model.delegation

import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_domain.ext.removeSubstring
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.util.Converter

data class GetExpiringVestingDelegationsResponseDTO(
    val jsonrpc: String?,
    val result: List<ExpiringVestingDelegationDTO>?,
    val id: Int?
)

data class ExpiringVestingDelegationDTO(
    val id: Int?,
    val delegator: String?,
    val vesting_shares: String?,
    val expiration: String?
) {
    fun toExpiringVestingDelegation(): ExpiringVestingDelegation = ExpiringVestingDelegation(
        id = id ?: 0,
        delegator = delegator ?: "",
        steemPower = vesting_shares ?: "",
        expiration = expiration ?: ""
    )

    fun toExpiringVestingDelegation(dgp: GetDynamicGlobalPropertiesDTO?): ExpiringVestingDelegation {
        if (dgp == null) return toExpiringVestingDelegation()
        val floatVestingShare = vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatTotalVestingShare = dgp.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatTotalVestingFundSteem = dgp.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f
        val steemPower = Converter.toSteemPowerFromVest(floatVestingShare, floatTotalVestingShare, floatTotalVestingFundSteem)
        return ExpiringVestingDelegation(
            id = id ?: 0,
            delegator = delegator ?: "",
            steemPower = String.format("%.3f SP", steemPower),
            expiration = expiration ?: ""
        )
    }
}
