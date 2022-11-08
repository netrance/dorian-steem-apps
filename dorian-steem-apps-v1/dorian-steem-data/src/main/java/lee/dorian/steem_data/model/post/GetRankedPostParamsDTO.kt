package lee.dorian.steem_data.model.post

data class GetRankedPostParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "bridge.get_ranked_postss",
    val params: List<Params>,
    val id: Int
) {

    data class Params(
        val sort: String = SORT_TRENDING,
        val tag: String = "",
        val observer: String = "",
        val limit: Int = 20,
        val start_author: String = "",
        val start_permlink: String = ""
    ) {

        companion object {
            const val SORT_TRENDING = "trending"
            const val SORT_HOT = "hot"
            const val SORT_CREATED = "created"
            const val SORT_PROMOTED = "promoted"
            const val SORT_PAYOUT = "payout"
            const val SORT_PAYOUT_COMMENTS = "payout_comments"
            const val SORT_MUTED = "muted"
        }

    }

}
