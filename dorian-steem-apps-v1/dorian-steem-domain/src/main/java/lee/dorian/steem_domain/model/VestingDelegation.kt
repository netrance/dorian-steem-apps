package lee.dorian.steem_domain.model

data class VestingDelegation(
    val id: Int = 0,
    val delegator: String = "",
    val delegatee: String = "",
    val steemPower: String = "",
    val minDelegationTime: String = ""
)
