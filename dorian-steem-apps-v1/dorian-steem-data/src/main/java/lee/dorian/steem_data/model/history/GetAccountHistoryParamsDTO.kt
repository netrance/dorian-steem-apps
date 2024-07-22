package lee.dorian.steem_data.model.history

data class GetAccountHistoryParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_account_history",
    val params: InnerParams,
    val id: Int
) {

    class InnerParams(val account: String, val start: Int, val limit: Int) : List<Any> by listOf(
        account,
        start,
        limit
    ) {
        companion object {
            const val DEFAULT_LIMIT = 20
        }
    }

}