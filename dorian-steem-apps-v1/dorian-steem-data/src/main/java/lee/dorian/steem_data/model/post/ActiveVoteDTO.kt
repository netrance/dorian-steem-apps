package lee.dorian.steem_data.model.post

data class ActiveVoteDTO(
    val voter: String?,
    val rshares: String?
) {

    fun isUpvote(): Boolean {
        return rshares?.toLongOrNull()?.let {
            when {
                (null == it) -> false
                (it >= 0) -> true
                else -> false
            }
        } ?: false
    }

}