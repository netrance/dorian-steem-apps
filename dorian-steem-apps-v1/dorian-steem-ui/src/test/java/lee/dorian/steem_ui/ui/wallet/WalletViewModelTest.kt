package lee.dorian.steem_ui.ui.wallet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadSteemitWalletUseCase
import org.junit.*

import org.junit.Assert.*

// To test WalletViewModel class
class WalletViewModelTest : CommonPartOfViewModelTest() {

    var walletViewModel = WalletViewModel(ReadSteemitWalletUseCase(SteemRepositoryImpl(), dispatcher))

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun readSteemitWallet_case1() {
        walletViewModel.readSteemitWallet("dorian-mobileapp")
        Thread.sleep(3000)

        val walletState = walletViewModel.flowWalletState.value
        assertTrue(walletState is WalletState.Success)
        val wallet = (walletState as WalletState.Success).wallet
        assertEquals("dorian-mobileapp", wallet.account)
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun readSteemitWallet_case2() {
        walletViewModel.readSteemitWallet("invalid10293845")
        Thread.sleep(3000)
        val walletState = walletViewModel.flowWalletState.value
        assertTrue(walletState is WalletState.Success)
        val wallet = (walletState as WalletState.Success).wallet
        assertEquals("", wallet.account)
    }

}