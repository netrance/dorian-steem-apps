package lee.dorian.steem_domain.model

data class Post(
    val title: String,
    val thumbnailURL: String,
    val tags: List<String>,
    val imageURLs: List<String>,
    val app: String,
    val communityTag: String,
    val communityTitle: String,
    val content: String,
    val time: String,
    val rewards: Float,
    val authorRewards: Float,
    val curationRewards: Float,
    val isPaidout: Boolean,
    val paidoutTime: String,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val activeVotes: List<ActiveVote>,
    val promoted: Float,
    val account: String,
    val reputation: Int,
    val permlink: String
)
