package lee.dorian.steem_data.repository

import lee.dorian.steem_data.constants.TestData
import org.junit.Test

import org.junit.Assert.*

class SteemRepositoryImplTest {

    val steemRepository = SteemRepositoryImpl()

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun readSteemitWallet_case1() {
        steemRepository.readSteemitWallet(TestData.singleAccount).subscribe { wallet ->
            assertEquals(1, wallet.size)
            assertEquals(TestData.singleAccount, wallet[0].account)
        }
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun readSteemitWallet_case2() {
        steemRepository.readSteemitWallet(TestData.invalidSingleAccount).subscribe { wallet ->
            assertEquals(0, wallet.size)
        }
    }

}