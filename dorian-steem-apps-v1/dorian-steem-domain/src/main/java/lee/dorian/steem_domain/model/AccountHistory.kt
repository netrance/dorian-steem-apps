package lee.dorian.steem_domain.model

data class AccountHistory(
    val account: String,
    val historyList: List<AccountHistoryItem>
)