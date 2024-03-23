package lee.dorian.steem_domain.model

data class PostListInfo(
    val author: String,
    val sort: String,
    val posts: List<PostItem>
)
