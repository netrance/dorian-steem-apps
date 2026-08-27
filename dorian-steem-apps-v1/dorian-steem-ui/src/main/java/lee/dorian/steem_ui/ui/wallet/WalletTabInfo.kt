package lee.dorian.steem_ui.ui.wallet

enum class WalletTabInfo(
    val title: String
) {
    BALANCE("Balance"),
    SENT("Transfer\n(sent)"),
    RECEIVED("Transfer\n(received)")
}
