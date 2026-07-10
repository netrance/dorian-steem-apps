package lee.dorian.steem_data.model.delegation

data class GetVestingDelegationsParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_vesting_delegations",
    val params: InnerParams,
    val id: Int
) {
    class InnerParams(
        val delegatorAccount: String,
        val startAccount: String,
        val limit: Int
    ) : List<Any> by listOf(delegatorAccount, startAccount, limit)
}
