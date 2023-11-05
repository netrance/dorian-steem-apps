package lee.dorian.steem_ui.ui.wallet

import lee.dorian.steem_domain.model.SteemitWallet

sealed interface WalletState {

    object Empty: WalletState

    object Loading: WalletState

    data class Success(val wallet: SteemitWallet) : WalletState

    data class Failure(val content: String): WalletState

    data class Error(val throwable: Throwable): WalletState

}
