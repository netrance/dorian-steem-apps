package lee.dorian.steem_data.repository

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ContentHistory
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Test

import org.junit.Assert.*

class SteemWorldRepositoryImplTest : CommonPartOfViewModelTest() {

    val steemWorldRepository = SteemWorldRepositoryImpl(dispatcher)

    // Every revision has to be a whole post rather than only the fields the edit changed,
    // because the screen draws a revision the same way it draws the live post.
    private fun assertEveryRevisionIsAWholePost(revisions: List<ContentHistory>) {
        for (revision in revisions) {
            // The time is formatted as "yyyy-MM-dd HH:mm".
            assertEquals(16, revision.time.length)
            assertTrue(revision.title.isNotEmpty())
            assertTrue(revision.body.isNotEmpty())
        }
    }

    // Test case 1: A post that was edited — every revision comes back as a whole post.
    @Test
    fun readContentHistory_case1() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val apiResult = steemWorldRepository.readContentHistory(author, permlink)
        assertTrue(apiResult is ApiResult.Success)

        val revisions = (apiResult as ApiResult.Success).data
        assertTrue(revisions.size > 1)
        assertEveryRevisionIsAWholePost(revisions)
    }

    // Test case 2: An invalid link — SDS answers with HTTP 200 and a non-zero code, so this
    // only fails if the body is checked as well as the response. Reading isSuccessful alone
    // turns a post that does not exist into an empty history, which is the mistake the
    // delegation reads still make. (see docs/TESTING.md)
    @Test
    fun readContentHistory_case2() = runTest {
        val (author, permlink) = TestData.invalidContentLink
        val apiResult = steemWorldRepository.readContentHistory(author, permlink)
        assertTrue(apiResult is ApiResult.Failure)
        assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 3: An existing author with a permlink that does not exist fails the same way.
    // SDS does not tell the two apart, and neither does the repository.
    @Test
    fun readContentHistory_case3() = runTest {
        val (author, permlink) = TestData.invalidPermlinkContentLink
        val apiResult = steemWorldRepository.readContentHistory(author, permlink)
        assertTrue(apiResult is ApiResult.Failure)
        assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 4: The revisions are reported latest first.
    //
    // This is the repository's own doing. content_history_api returns them oldest first and
    // has no parameter to ask for the other order, so the reversal happens here, where every
    // other SteemWorldRepository read also promises the newest item first.
    @Test
    fun readContentHistory_case4() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val apiResult = steemWorldRepository.readContentHistory(author, permlink)
        assertTrue(apiResult is ApiResult.Success)

        val revisions = (apiResult as ApiResult.Success).data
        assertTrue(revisions.size > 1)
        // The time is formatted as "yyyy-MM-dd HH:mm", so it sorts the same as the instant.
        for (i in 1 until revisions.size) {
            assertTrue(revisions[i - 1].time >= revisions[i].time)
        }
    }

    // Test case 5: A post that has never been edited has exactly one revision, the post as it
    // was published, so an empty history never means "this post was never edited".
    @Test
    fun readContentHistory_case5() = runTest {
        val (author, permlink) = TestData.singleRevisionContentLink
        val apiResult = steemWorldRepository.readContentHistory(author, permlink)
        assertTrue(apiResult is ApiResult.Success)

        val revisions = (apiResult as ApiResult.Success).data
        assertEquals(1, revisions.size)
        assertEveryRevisionIsAWholePost(revisions)
    }

}
