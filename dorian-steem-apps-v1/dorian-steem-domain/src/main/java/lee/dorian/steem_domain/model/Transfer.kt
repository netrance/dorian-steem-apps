package lee.dorian.steem_domain.model

data class Transfer(
    val time: String = "",       // Local time, formatted as "yyyy-MM-dd HH:mm"
    val from: String = "",
    val to: String = "",
    val amount: String = "",     // e.g. "18.868 STEEM"
    val memo: String = ""
)
