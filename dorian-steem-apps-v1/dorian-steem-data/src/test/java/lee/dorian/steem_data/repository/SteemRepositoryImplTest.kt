package lee.dorian.steem_data.repository

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.constants.TestData
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Test

import org.junit.Assert.*

class SteemRepositoryImplTest {

    val steemRepository = SteemRepositoryImpl()

    // Test case 1: Trying to get the wallet of a valid account.
    @Test
    fun readSteemitWallet_case1() = runTest {
        val apiResult = steemRepository.readSteemitWallet(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val wallets = (apiResult as ApiResult.Success).data
        assertEquals(1, wallets.size)
        assertEquals(TestData.singleAccount, wallets[0].account)
    }

    // Test case 2: Trying to get the wallet of an invalid account.
    @Test
    fun readSteemitWallet_case2() = runTest {
        val apiResult = steemRepository.readSteemitWallet(TestData.invalidSingleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val wallets = (apiResult as ApiResult.Success).data
        assertEquals(0, wallets.size)
    }

    @Test
    fun readRankedPosts() = runTest {
        val apiResult = steemRepository.readRankedPosts(
            "trending",
            "kr",
            "",
            GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT,
            listOf()
        )
        assertTrue(apiResult is ApiResult.Success)
        val postItemList = (apiResult as ApiResult.Success).data
        for (postItem in postItemList) {
            assertTrue(postItem.account.isNotEmpty())
            assertTrue(postItem.permlink.isNotEmpty())
            assertTrue(postItem.title.isNotEmpty())
            assertTrue(postItem.content.isNotEmpty())
        }
    }

}