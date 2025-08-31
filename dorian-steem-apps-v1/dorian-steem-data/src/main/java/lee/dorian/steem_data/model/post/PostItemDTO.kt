package lee.dorian.steem_data.model.post

import lee.dorian.steem_domain.model.Post
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
    val replies: List<String>?,    // it is empty list
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
        val imageURLs = json_metadata?.image ?: listOf()
        val tag = category ?: ""
        val communityTitle = community_title ?: ""
        val tagOrCommunity = when {
            (communityTitle.isEmpty()) -> tag
            else -> communityTitle
        }
        val voteCount = active_votes?.size ?: 0
        val upvotes = active_votes?.filter { activeVote -> activeVote.isUpvote() }
        val upvoteCount = upvotes?.size ?: 0
        val downvotes = active_votes?.filter { activeVote -> activeVote.isDownvote() }
        val downvoteCount = downvotes?.size ?: 0
        val activeVotes = active_votes?.map { currentVoteDTO ->
            currentVoteDTO.toActiveVote(net_rshares ?: 0L, payout ?: 0f)
        } ?: listOf()

        return PostItem(
            title ?: "",
            thumbnailURL,
            imageURLs,
            body ?: "",
            tagOrCommunity,
            Converter.toLocalTimeFromUTCTime(created ?: "", "yyyy-MM-dd HH:mm"),
            payout ?: 0f,
            upvoteCount,
            downvoteCount,
            children ?: 0,
            activeVotes,
            author ?: "",
            author_reputation?.toInt() ?: 0,
            permlink ?: ""
        )
    }

    fun toPost(): Post {
        val thumbnailURL = json_metadata?.getThumbnailURL() ?: ""
        val tags = json_metadata?.getTags() ?: listOf()
        val imageURLs = json_metadata?.image ?: listOf()
        val app = json_metadata?.app ?: ""
        val communityTag = community ?: ""
        val communityTitle = community_title ?: ""
        val authorRewards = author_payout_value?.replace(" SBD", "")?.toFloat() ?: 0f
        val curationRewards = curator_payout_value?.replace(" SBD", "")?.toFloat() ?: 0f
        val promoted = promoted?.replace(" SBD", "")?.toFloat() ?: 0f
        val voteCount = active_votes?.size ?: 0
        val upvotes = active_votes?.filter { activeVote -> activeVote.isUpvote() }
        val upvoteCount = upvotes?.size ?: 0
        val downvotes = active_votes?.filter { activeVote -> activeVote.isDownvote() }
        val downvoteCount = downvotes?.size ?: 0
        val activeVotes = active_votes?.map { currentVoteDTO ->
            currentVoteDTO.toActiveVote(net_rshares ?: 0L, payout ?: 0f)
        } ?: listOf()

        return Post(
            title ?: "",
            thumbnailURL,
            tags,
            imageURLs,
            app,
            communityTag,
            communityTitle,
            body ?: "",
            Converter.toLocalTimeFromUTCTime(created ?: "", "yyyy-MM-dd HH:mm"),
            depth ?: 0,
            payout ?: 0f,
            authorRewards,
            curationRewards,
            is_payout ?: false,
            Converter.toLocalTimeFromUTCTime(payout_at ?: "", "yyyy-MM-dd HH:mm"),
            upvoteCount,
            downvoteCount,
            activeVotes,
            promoted,
            author ?: "",
            author_reputation?.toInt() ?: 0,
            permlink ?: ""
        )
    }
}
