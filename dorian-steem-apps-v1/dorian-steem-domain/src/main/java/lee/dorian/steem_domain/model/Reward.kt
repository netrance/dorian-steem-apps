package lee.dorian.steem_domain.model

data class Reward(
    val time: String = "",                       // Local time, formatted as "yyyy-MM-dd HH:mm"
    val type: RewardType = RewardType.AUTHOR,
    val amount: String = "",                     // e.g. "0.323 SBD, 78.416 SP"
    val author: String = "",                     // The author of the rewarded post
    val permlink: String = ""                    // The permlink of the rewarded post
)
