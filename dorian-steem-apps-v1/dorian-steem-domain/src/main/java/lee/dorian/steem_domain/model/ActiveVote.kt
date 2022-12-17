package lee.dorian.steem_domain.model

data class ActiveVote(
    val voter: String,
    val weight: Float,
    val value: Float
) : java.io.Serializable {

    fun readWeightAsPercent(): String = String.format("%.2f", weight)

    fun readValueAsSTU(): String = when {
        (value >= 0f) -> String.format("$%.3f", value)
        else -> String.format("-$%.3f", -value)
    }

    fun isUpvote(): Boolean = when {
        (value >= 0f) -> true
        else -> false
    }

    fun isDownvote(): Boolean = when {
        (value < 0f) -> true
        else -> false
    }

}