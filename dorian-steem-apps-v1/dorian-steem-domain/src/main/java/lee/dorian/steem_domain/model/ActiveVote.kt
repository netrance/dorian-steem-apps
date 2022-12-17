package lee.dorian.steem_domain.model

data class ActiveVote(
    val voter: String,
    val weight: String,
    val value: String
) : java.io.Serializable
