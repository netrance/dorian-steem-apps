package lee.dorian.steem_domain.model

data class AccountHistoryItem(
    val index: Int,
    val localTime: String,
    val type: String,
    val content: Map<String, Any>
)
