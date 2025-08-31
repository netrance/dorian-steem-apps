package lee.dorian.steem_domain.model

data class PostItem(
    val title: String,
    val thumbnailURL: String,
    val imageURLs: List<String>,
    val content: String,
    val tagOrCommunity: String,
    val time: String,
    val rewards: Float,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val replyCount: Int,
    val activeVotes: List<ActiveVote>,
    val account: String,
    val reputation: Int,
    val permlink: String
)
