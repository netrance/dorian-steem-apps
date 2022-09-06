package lee.dorian.steem_domain

data class SteemitWalletDTO(
    val account: String,
    val steemBalance: String,
    val sbdBalance: String,
    val savingSteemBalance: String,
    val savingSbdBalance: String,
    val steemPowerVest: String,
    val delegatedSteemPowerVest: String,
    val receivedSteemPowerVest: String,
    val spWithdrawRate: String,
    val spToBeWithdrawn: String
)