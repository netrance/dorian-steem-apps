package lee.dorian.steem_domain.usecase

import lee.dorian.steem_data.constants.TestData
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import org.junit.Assert
import org.junit.Test

class ReadSteemitWalletUseCaseTest {

    val readSteemitWalletUseCase = ReadSteemitWalletUseCase(SteemRepositoryImpl())

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun invoke_case1() {
        readSteemitWalletUseCase(TestData.singleAccount).subscribe { wallets ->
            Assert.assertEquals(1, wallets.size)
            Assert.assertEquals(TestData.singleAccount, wallets[0].account)
        }
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun invoke_case2() {
        readSteemitWalletUseCase(TestData.invalidSingleAccount).subscribe { wallets ->
            Assert.assertEquals(0, wallets.size)
        }
    }

}