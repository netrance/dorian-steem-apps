package lee.dorian.steem_domain.model

data class SteemitProfile(
    val account: String = "",
    val name: String = "",
    val about: String = "",
    val followingCount: Int = 0,
    val followerCount: Int = 0,
    val location: String = "",
    val website: String = "",
    val coverImageURL: String = ""
)