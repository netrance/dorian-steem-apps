package lee.dorian.steem_data.model.post

data class GetAccountPostsResponseDTO(
    val jsonrpc: String? = "2.0",
    val result: List<PostItemDTO>? = listOf()
)
