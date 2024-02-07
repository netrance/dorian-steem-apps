package lee.dorian.steem_data.model.post

data class GetAccountPostParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "bridge.get_account_posts",
    val params: InnerParams,
    val id: Int
) {

    data class InnerParams(
        val account: String,
        val sort: String = SORT_BLOG,
        val observer: String = "",
        val limit: Int = DEFAULT_LIMIT,
        val start_author: String = "",
        val start_permlink: String = ""
    ) {

        companion object {
            const val SORT_BLOG = "blog"
            const val SORT_POSTS = "posts"

            const val DEFAULT_LIMIT = 20
        }

    }

}
