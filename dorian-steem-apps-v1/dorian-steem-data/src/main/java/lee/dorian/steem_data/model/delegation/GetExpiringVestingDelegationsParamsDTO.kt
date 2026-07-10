package lee.dorian.steem_data.model.delegation

data class GetExpiringVestingDelegationsParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_expiring_vesting_delegations",
    val params: InnerParams,
    val id: Int
) {
    class InnerParams(
        val account: String,
        val after: String
    ) : List<Any> by listOf(account, after)
}
