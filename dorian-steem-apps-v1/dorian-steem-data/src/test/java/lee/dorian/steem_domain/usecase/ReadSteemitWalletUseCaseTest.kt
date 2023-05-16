package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.constants.TestData
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadSteemitWalletUseCaseTest {

    val readSteemitWalletUseCase = ReadSteemitWalletUseCase(
        SteemRepositoryImpl()
    )

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun invoke_case1() = runTest {
        val apiResult = readSteemitWalletUseCase(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val wallets = (apiResult as ApiResult.Success).data
        assertEquals(1, wallets.size)
        assertEquals(TestData.singleAccount, wallets[0].account)
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun invoke_case2() = runTest {
        val apiResult = readSteemitWalletUseCase(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val wallets = (apiResult as ApiResult.Success).data
        assertEquals(0, wallets.size)
    }

}