package lee.dorian.steem_domain.model

data class FollowCount(
    val account: String,
    val followingCount: Int,
    val followerCount: Int
)
