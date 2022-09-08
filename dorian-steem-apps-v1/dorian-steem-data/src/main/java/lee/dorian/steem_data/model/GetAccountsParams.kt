package lee.dorian.steem_data.model

data class GetAccountsParams(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_accounts",
    val params: Array<Array<String>>,
    val id: Int
)
