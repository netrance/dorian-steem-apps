package lee.dorian.steem_domain.model

data class Blog(
    val author: String,
    val posts: List<PostItem>
)
