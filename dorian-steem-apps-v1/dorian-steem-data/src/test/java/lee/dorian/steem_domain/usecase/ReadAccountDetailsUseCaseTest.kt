package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadAccountDetailsUseCaseTest {

    val readAccountDetailsUseCase = ReadAccountDetailsUseCase(
        SteemRepositoryImpl()
    )

    // Test case 1: Trying to get the profile of a valid account.
    @Test
    fun invoke_case1() = runTest {
        val apiResult = readAccountDetailsUseCase(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val accountDetails = (apiResult as ApiResult.Success).data
        assertEquals(TestData.singleAccount, accountDetails.name)
    }

    // Test case 2: Trying to get the profile of an invalid account.
    @Test
    fun invoke_case2() = runTest {
        val apiResult = readAccountDetailsUseCase(TestData.invalidSingleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val accountDetails = (apiResult as ApiResult.Success).data
        assertTrue(accountDetails.name.isEmpty())
    }

}