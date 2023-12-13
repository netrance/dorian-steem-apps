package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadSteemitProfileUseCaseTest {

    val readSteemitProfileUseCase = ReadSteemitProfileUseCase(
        SteemRepositoryImpl()
    )

    // Test case 1: Trying to get the profile of a valid account.
    @Test
    fun invoke_case1() = runTest {
        val apiResult = readSteemitProfileUseCase(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val profile = (apiResult as ApiResult.Success).data
        assertTrue(profile.account.isNotEmpty())
        assertTrue(profile.followingCount > 0)
        assertTrue(profile.followerCount > 0)
    }

    // Test case 2: Trying to get the profile of an invalid account.
    @Test
    fun invoke_case2() = runTest {
        val apiResult = readSteemitProfileUseCase(TestData.invalidSingleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val profile = (apiResult as ApiResult.Success).data
        assertTrue(profile.account.isEmpty())
        assertTrue(profile.followingCount == 0)
        assertTrue(profile.followerCount == 0)
    }

}