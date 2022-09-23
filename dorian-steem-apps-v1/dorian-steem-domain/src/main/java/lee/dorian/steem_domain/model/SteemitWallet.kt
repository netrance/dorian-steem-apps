package lee.dorian.steem_domain.model

data class SteemitWallet(
    val account: String = "",
    val steemBalance: String = "0 STEEM",
    val sbdBalance: String = "0 SBD",
    val savingSteemBalance: String = "0 STEEM",
    val savingSbdBalance: String = "0 SBD",
    val steemPowerVest: String = "0 VESTS",
    val delegatedSteemPowerVest: String = "0 VESTS",
    val receivedSteemPowerVest: String = "0 VESTS",
    val spWithdrawRate: String = "0 VESTS",
    val spToBeWithdrawn: String = "0 VESTS"
)