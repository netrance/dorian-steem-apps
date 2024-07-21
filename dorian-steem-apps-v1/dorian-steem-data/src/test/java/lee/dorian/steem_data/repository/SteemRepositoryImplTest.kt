package lee.dorian.steem_data.repository

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Test

import org.junit.Assert.*

class SteemRepositoryImplTest {

    val steemRepository = SteemRepositoryImpl()

    // Test case 1: Trying to get the profile of a valid account.
    @Test
    fun readSteemitProfile_case1() = runTest {
        val account = "dorian-lee"
        val apiResult = steemRepository.readSteemitProfile(account)
        assertTrue(apiResult is ApiResult.Success)

        val profile = (apiResult as ApiResult.Success).data
        assertEquals(account, profile.account)
        assertTrue(profile.followingCount > 0)
        assertTrue(profile.followerCount > 0)
    }

    // Test case 2: Trying to get the profile of an invalid account.
    @Test
    fun readSteemitProfile_case2() = runTest {
        val apiResult = steemRepository.readSteemitProfile(TestData.invalidSingleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val profile = (apiResult as ApiResult.Success).data
        assertEquals("", profile.account)
        assertTrue(profile.followingCount == 0)
        assertTrue(profile.followerCount == 0)
    }

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
    fun readPosts() = runTest {
        val apiResult = steemRepository.readPosts(
            TestData.singleAccount,
            "posts",
            "",
            30,
            mutableListOf()
        )

        assertTrue(apiResult is ApiResult.Success)
        val postItemList = (apiResult as ApiResult.Success).data
        for (postItem in postItemList) {
            assertTrue(postItem.account == TestData.singleAccount)
            assertTrue(postItem.permlink.isNotEmpty())
            assertTrue(postItem.title.isNotEmpty())
            assertTrue(postItem.content.isNotEmpty())
        }
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

    @Test
    fun readPost() = runTest {
        val apiResult = steemRepository.readPostAndReplies(
            "dorian-lee",
            "1000"
        )

        assertTrue(apiResult is ApiResult.Success)
        val postList = (apiResult as ApiResult.Success).data
        assertTrue(postList.size > 0)
        assertTrue(postList[0].account == "dorian-lee")
        assertTrue(postList[0].permlink == "1000")
    }

    @Test
    fun readAccountHistory() = runTest {
        val apiResult = steemRepository.readAccountHistory(TestData.singleAccount2, 20, listOf())
        assertTrue(apiResult is ApiResult.Success)

        val accountHistoryList = (apiResult as ApiResult.Success).data
        assertTrue(accountHistoryList.size == 20 + 1)

        val apiResult2 = steemRepository.readAccountHistory(TestData.singleAccount2, 20, accountHistoryList)
        val accountHistoryList2 = (apiResult2 as ApiResult.Success).data
        assertTrue(accountHistoryList2.size == 20 + 1)
    }

}