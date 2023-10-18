package lee.dorian.steem_data.model.post

data class PostStatsDTO(
    val hide: Boolean?,
    val gray: Boolean?,
    val total_votes: Int?,
    val flag_weight: Float?,
    val is_pinned: Boolean?
)
