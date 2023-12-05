package lee.dorian.steem_data.model.follow

import lee.dorian.steem_domain.model.FollowCount

data class GetFollowCountResponseDTO(
    val jsonrpc: String?,
    val result: Result,
    val id: Int?
) {

    data class Result(
        val account: String?,
        val following_count: Int,
        val follower_count: Int
    )

    fun toFollowCount(): FollowCount = FollowCount(
        result.account ?: "",
        result.following_count ?: 0,
        result.follower_count ?: 0
    )

}