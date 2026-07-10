package lee.dorian.steem_data.model.delegation

import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_domain.ext.removeSubstring
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.util.Converter

data class GetVestingDelegationsResponseDTO(
    val jsonrpc: String?,
    val result: List<VestingDelegationDTO>?,
    val id: Int?
)

data class VestingDelegationDTO(
    val id: Int?,
    val delegator: String?,
    val delegatee: String?,
    val vesting_shares: String?,
    val min_delegation_time: String?
) {
    fun toVestingDelegation(): VestingDelegation {
        return VestingDelegation(
            id = id ?: 0,
            delegator = delegator ?: "",
            delegatee = delegatee ?: "",
            steemPower = vesting_shares ?: "",
            minDelegationTime = min_delegation_time ?: ""
        )
    }

    fun toVestingDelegation(getDynamicGlobalPropertiesDTO: GetDynamicGlobalPropertiesDTO?): VestingDelegation {
        return if (getDynamicGlobalPropertiesDTO == null) {
            toVestingDelegation()
        } else {
            val floatVestingShare = vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
            val floatTotalVestingShare = getDynamicGlobalPropertiesDTO.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
            val floatTotalVestingFundSteem = getDynamicGlobalPropertiesDTO.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f
            val steemPower = Converter.toSteemPowerFromVest(
                floatVestingShare,
                floatTotalVestingShare,
                floatTotalVestingFundSteem
            )
            VestingDelegation(
                id = id ?: 0,
                delegator = delegator ?: "",
                delegatee = delegatee ?: "",
                steemPower = String.format("%.3f SP", steemPower),
                minDelegationTime = min_delegation_time ?: ""
            )
        }
    }

}
