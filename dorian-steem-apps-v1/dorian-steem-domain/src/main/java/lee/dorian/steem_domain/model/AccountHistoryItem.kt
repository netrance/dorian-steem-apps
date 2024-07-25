package lee.dorian.steem_domain.model

data class AccountHistoryItem(
    val index: Int,
    val timestamp: String,
    val content: Map<String, Any>
)
