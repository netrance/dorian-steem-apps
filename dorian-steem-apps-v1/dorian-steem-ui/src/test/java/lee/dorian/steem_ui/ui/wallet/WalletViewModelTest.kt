package lee.dorian.steem_ui.ui.wallet

import org.junit.*

import org.junit.Assert.*

// To test WalletViewModel class
class WalletViewModelTest : CommonPartOfViewModelTest() {

    var walletViewModel = WalletViewModel()

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun readSteemitWallet_case1() {
        walletViewModel.readSteemitWallet("dorian-mobileapp").subscribe { steemitWallets ->
            assertEquals("dorian-mobileapp", walletViewModel.steemitWallet.value?.account)
        }
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun readSteemitWallet_case2() {
        walletViewModel.readSteemitWallet("invalid10293845").subscribe { steemitWallets ->
            assertEquals("", walletViewModel.steemitWallet.value?.account)
        }
    }

}