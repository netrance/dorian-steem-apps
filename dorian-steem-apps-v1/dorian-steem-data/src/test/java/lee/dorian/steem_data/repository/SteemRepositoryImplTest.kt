package lee.dorian.steem_data.repository

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_test.CommonPartOfViewModelTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

import org.junit.Assert.*

class SteemRepositoryImplTest : CommonPartOfViewModelTest() {

    val steemRepository = SteemRepositoryImpl(dispatcher)

    @Test
    fun readAccountDetails_case1() = runTest {
        val apiResult = steemRepository.readAccountDetails(TestData.singleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val accountDetails = (apiResult as ApiResult.Success).data
        assertEquals(TestData.singleAccount, accountDetails.name)
    }

    @Test
    fun readAccountDetails_case2() = runTest {
        val apiResult = steemRepository.readAccountDetails(TestData.invalidSingleAccount)
        assertTrue(apiResult is ApiResult.Success)

        val accountDetails = (apiResult as ApiResult.Success).data
        assertEquals("", accountDetails.name)
    }

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

    /**
     * readPosts 가 작성 앱에 상관없이 파싱되는지 확인한다.
     *
     * 글의 json_metadata 모양은 글을 올린 앱마다 다르고, Gson 은 선언한 타입과 어긋나면
     * 그 글 하나가 아니라 응답 전체의 파싱을 포기한다. 그래서 한 계정만으로 테스트하면
     * 다른 앱에서만 나타나는 모양을 놓친다.
     *
     * 개별 글의 내용이 아니라 "파싱에 성공했는가"만 단언한다. 계정이 글을 지우거나
     * 활동을 멈춰도 깨지지 않게 하기 위해서다.
     */
    @Test
    fun readPosts_acrossPostingApps() = runTest(timeout = 180.seconds) {
        val failures = mutableListOf<String>()

        for ((account, app) in TestData.accountsByPostingApp) {
            val apiResult = steemRepository.readPosts(account, "posts", "", 30, mutableListOf())
            when (apiResult) {
                is ApiResult.Success -> {
                    for (postItem in apiResult.data) {
                        assertTrue(postItem.account == account)
                        assertTrue(postItem.permlink.isNotEmpty())
                    }
                }
                is ApiResult.Failure -> failures.add("$account ($app): Failure(${apiResult.content})")
                is ApiResult.Error -> failures.add("$account ($app): ${apiResult.throwable}")
            }
        }

        assertTrue(failures.joinToString(prefix = "\n", separator = "\n"), failures.isEmpty())
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