package lee.dorian.steem_domain.model

data class ExpiringVestingDelegation(
    val id: Int = 0,
    val delegator: String = "",
    val steemPower: String = "",
    val expiration: String = ""
)
