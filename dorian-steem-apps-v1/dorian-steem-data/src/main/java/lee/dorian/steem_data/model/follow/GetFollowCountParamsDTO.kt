package lee.dorian.steem_data.model.follow

data class GetFollowCountParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_follow_count",
    val params: Array<String>,
    val id: Int
)
