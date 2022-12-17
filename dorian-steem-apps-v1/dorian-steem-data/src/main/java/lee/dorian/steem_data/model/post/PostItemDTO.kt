package lee.dorian.steem_data.model.post

import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.util.Converter

data class PostItemDTO(
    val post_id: String?,
    val author: String?,
    val permlink: String?,
    val category: String?,
    val title: String?,
    val body: String?,
    val json_metadata: JSONMetadataDTO?,
    val created: String?,
    val updated: String?,
    val depth: Int?,
    val children: Int?,
    val net_rshares: Long?,
    val is_payout: Boolean?,
    val payout_at: String?,
    val payout: Float?,
    val pending_payout_value: String?,
    val author_payout_value: String?,
    val curator_payout_value: String?,
    val promoted: String?,
    val replies: List<Any>?,    // it is empty list
    val active_votes: List<ActiveVoteDTO>?,
    val author_reputation: Float?,
    val stats: PostStatsDTO?,
    val beneficiaries: List<PostBeneficiaryDTO>?,
    val max_accepted_payout: String?,
    val percent_steem_dollars: Int?,
    val url: String?,
    val blacklists: List<String>?,
    val community: String?,
    val community_title: String?,
    val author_role: String?,
    val author_title: String?
) {

    fun toPostItem(): PostItem {
        val thumbnailURL = json_metadata?.getThumbnailURL() ?: ""
        val tag = category ?: ""
        val communityTitle = community_title ?: ""
        val tagOrCommunity = when {
            (communityTitle.isEmpty()) -> tag
            else -> communityTitle
        }
        val voteCount = active_votes?.size ?: 0
        val upvotes = active_votes?.filter { activeVote -> activeVote.isUpvote() }
        val upvoteCount = upvotes?.size ?: 0
        val downvoteCount = voteCount - upvoteCount
        val activeVotes = active_votes?.map { currentVoteDTO ->
            currentVoteDTO.toActiveVote(net_rshares ?: 0L, payout ?: 0f)
        } ?: listOf()

        return PostItem(
            title ?: "",
            thumbnailURL,
            body ?: "",
            tagOrCommunity,
            Converter.toLocalTimeFromUTCTime(created ?: "", "yyyy-MM-dd HH:mm"),
            payout ?: 0f,
            upvoteCount,
            downvoteCount,
            activeVotes,
            author ?: "",
            author_reputation?.toInt() ?: 0,
            permlink ?: ""
        )
    }

}
