package lee.dorian.steem_data.model.post

import lee.dorian.steem_domain.model.ActiveVote

data class ActiveVoteDTO(
    val voter: String?,
    val rshares: String?,
    val percent: String?,
    val reputation: String?
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

    fun toActiveVote(netRshares: Long, payout: Float): ActiveVote {
        val currentRshares = this.rshares?.toLongOrNull() ?: 0L
        val votingWeight = when {
            (null == this.percent) -> ""
            else -> String.format("%.2f%", (this.percent.toIntOrNull() ?: 0) / 100f)
        }
        val votingValue = when {
            (null == payout) -> ""
            else -> String.format("$%.3f", payout * (currentRshares.toFloat() / netRshares.toFloat()))
        }

        return ActiveVote(this.voter ?: "", votingWeight, votingValue)
    }
}