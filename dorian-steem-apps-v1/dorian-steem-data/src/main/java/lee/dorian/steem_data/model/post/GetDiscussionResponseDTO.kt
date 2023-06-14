package lee.dorian.steem_data.model.post

data class GetDiscussionResponseDTO(
    val jsonrpc: String? = "2.0",
    val result: Map<String, PostItemDTO>? = mapOf()
)
