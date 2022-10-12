package lee.dorian.steem_domain.model

data class SteemitWallet(
    val account: String = "",
    val steemBalance: String = "- STEEM",
    val sbdBalance: String = "- SBD",
    val savingSteemBalance: String = "- STEEM",
    val savingSbdBalance: String = "- SBD",
    val steemPower: String = "- SP",
    val effectiveSteemPower: String = "- SP",
    val delegatedSteemPower: String = "- SP",
    val receivedSteemPower: String = "- SP",
    val spWithdrawRate: String = "- SP",
    val totalSPToBeWithdrawn: String = "- SP",
    val remainingSPToBeWithdrawn: String = "- SP"
)