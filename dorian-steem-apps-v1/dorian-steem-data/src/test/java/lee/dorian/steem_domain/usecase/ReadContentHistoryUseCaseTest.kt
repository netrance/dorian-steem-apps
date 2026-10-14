package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemWorldRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ContentHistory
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Assert
import org.junit.Test

class ReadContentHistoryUseCaseTest : CommonPartOfViewModelTest() {

    private val readContentHistoryUseCase = ReadContentHistoryUseCase(
        SteemWorldRepositoryImpl(dispatcher),
        dispatcher
    )

    // Every revision has to be a whole post, not just the fields the edit changed, so none of
    // the fields the screen draws may be empty.
    private fun testContentHistoryList(contentHistoryList: List<ContentHistory>) {
        for (contentHistory in contentHistoryList) {
            // The time is formatted as "yyyy-MM-dd HH:mm".
            Assert.assertEquals(16, contentHistory.time.length)
            Assert.assertTrue(contentHistory.title.isNotEmpty())
            Assert.assertTrue(contentHistory.body.isNotEmpty())
        }
    }

    // Test case 1: A post that was edited — every revision comes back as a whole post.
    @Test
    fun readContentHistory_case1() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size > 1)
            testContentHistoryList(data)
        }
    }

    // Test case 2: An invalid link — SDS answers with HTTP 200 and a non-zero code,
    // which has to be reported as a failure rather than as an empty history.
    @Test
    fun readContentHistory_case2() = runTest {
        val (author, permlink) = TestData.invalidContentLink
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 3: An existing author with a permlink that does not exist fails the same way,
    // so a post cannot be mistaken for one that exists but was never edited.
    @Test
    fun readContentHistory_case3() = runTest {
        val (author, permlink) = TestData.invalidPermlinkContentLink
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 4: The revisions are reported latest first, which is the opposite of the order
    // content_history_api returns them in. The screen depends on this, and the API has no
    // parameter to ask for it, so the reversal is the repository's job.
    @Test
    fun readContentHistory_case4() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size > 1)
            // The time is formatted as "yyyy-MM-dd HH:mm", so it sorts the same as the instant.
            for (i in 1 until data.size) {
                Assert.assertTrue(data[i - 1].time >= data[i].time)
            }
        }
    }

    // Test case 5: A post that has never been edited has exactly one revision, so an empty
    // history never means "not edited".
    @Test
    fun readContentHistory_case5() = runTest {
        val (author, permlink) = TestData.singleRevisionContentLink
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertEquals(1, data.size)
            testContentHistoryList(data)
        }
    }

    // Test case 6: SDS adds "image":{} — an empty object where the official API uses an array —
    // to the json_metadata of a revision with no images. Parsing it with a DTO that declares
    // "image" as a list makes Gson give up on the whole object and lose the tags with it.
    @Test
    fun readContentHistory_case6() = runTest {
        val (author, permlink) = TestData.contentLinkWithoutImages
        val apiResult = readContentHistoryUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.isNotEmpty())
            testContentHistoryList(data)
            for (contentHistory in data) {
                Assert.assertTrue(contentHistory.tags.isNotEmpty())
                Assert.assertTrue(contentHistory.app.isNotEmpty())
            }
        }
    }

}
