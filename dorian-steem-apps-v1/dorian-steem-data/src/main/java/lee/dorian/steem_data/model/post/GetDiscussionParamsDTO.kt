package lee.dorian.steem_data.model.post

data class GetDiscussionParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "bridge.get_discussion",
    val params: InnerParams,
    val id: Int
) {

    data class InnerParams(
        val author: String,
        val permlink: String
    )

}
