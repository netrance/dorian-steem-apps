package lee.dorian.steem_data.retrofit

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_domain.model.RewardType
import lee.dorian.steem_test.TestData
import org.junit.Assert.*
import org.junit.Test

class SteemWorldServiceTest {

    // Test case 1: Valid account — response is successful, rows are non-empty,
    // and each row has the correct format: [timestamp, from, to, vests].
    @Test
    fun getIncomingDelegations_case1() = runTest {
        val response = SteemWorldClient.apiService.getIncomingDelegations(TestData.singleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            val rows = body.result?.rows ?: listOf()
            for (row in rows) {
                assertEquals(4, row.size)
                assertTrue(row[0] is Double)   // time (Unix timestamp)
                assertTrue(row[1] is String)   // from (delegator)
                assertTrue(row[2] is String)   // to (delegatee)
                assertTrue(row[3] is Double)   // vests
                assertTrue((row[3] as Double) > 0.0)
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, rows are empty.
    @Test
    fun getIncomingDelegations_case2() = runTest {
        val response = SteemWorldClient.apiService.getIncomingDelegations(TestData.invalidSingleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            assertEquals(0, body.result?.rows?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 1: Valid account — response is successful, rows are non-empty,
    // and each row has the correct format: [timestamp, from, to, vests].
    @Test
    fun getOutgoingDelegations_case1() = runTest {
        val response = SteemWorldClient.apiService.getOutgoingDelegations(TestData.singleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            val rows = body.result?.rows ?: listOf()
            for (row in rows) {
                assertEquals(4, row.size)
                assertTrue(row[0] is Double)   // time (Unix timestamp)
                assertTrue(row[1] is String)   // from (delegator)
                assertTrue(row[2] is String)   // to (delegatee)
                assertTrue(row[3] is Double)   // vests
                assertTrue((row[3] as Double) > 0.0)
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, rows are empty.
    @Test
    fun getOutgoingDelegations_case2() = runTest {
        val response = SteemWorldClient.apiService.getOutgoingDelegations(TestData.invalidSingleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            assertEquals(0, body.result?.rows?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 1: Valid account — response is successful, rows are non-empty,
    // and each row has the correct format: [time, expiration, from, to, vests].
    @Test
    fun getExpiringDelegations_case1() = runTest {
        val response = SteemWorldClient.apiService.getExpiringDelegations(TestData.singleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            val rows = body.result?.rows ?: listOf()
            for (row in rows) {
                assertEquals(5, row.size)
                assertTrue(row[0] is Double)   // time (Unix timestamp)
                assertTrue(row[1] is Double)   // expiration (Unix timestamp)
                assertTrue(row[2] is String)   // from (delegator)
                assertTrue(row[3] is String)   // to (delegatee)
                assertTrue(row[4] is Double)   // vests
                assertTrue((row[4] as Double) > 0.0)
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, rows are empty.
    @Test
    fun getExpiringDelegations_case2() = runTest {
        val response = SteemWorldClient.apiService.getExpiringDelegations(TestData.invalidSingleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNotNull(body.result)
            assertEquals(0, body.result?.rows?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 1: Valid account — response is successful, and every transfer is sent by the account.
    @Test
    fun getTransfersByTypeFrom_case1() = runTest {
        val response = SteemWorldClient.apiService.getTransfersByTypeFrom(TestData.singleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNull(body.error)
            assertNotNull(body.result)
            val transfers = body.result?.toTransferList() ?: listOf()
            for (transfer in transfers) {
                assertEquals(TestData.singleAccount, transfer.from)
                assertTrue(transfer.to.isNotEmpty())
                assertTrue(transfer.amount > 0f)
                assertTrue(transfer.unit.isNotEmpty())
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, but the body contains an error.
    @Test
    fun getTransfersByTypeFrom_case2() = runTest {
        val response = SteemWorldClient.apiService.getTransfersByTypeFrom(TestData.invalidSingleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            assertNull(body.result)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 1: Valid account — response is successful, and every transfer is received by the account.
    @Test
    fun getTransfersByTypeTo_case1() = runTest {
        val response = SteemWorldClient.apiService.getTransfersByTypeTo(TestData.singleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNull(body.error)
            assertNotNull(body.result)
            val transfers = body.result?.toTransferList() ?: listOf()
            for (transfer in transfers) {
                assertEquals(TestData.singleAccount, transfer.to)
                assertTrue(transfer.from.isNotEmpty())
                assertTrue(transfer.amount > 0f)
                assertTrue(transfer.unit.isNotEmpty())
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, but the body contains an error.
    @Test
    fun getTransfersByTypeTo_case2() = runTest {
        val response = SteemWorldClient.apiService.getTransfersByTypeTo(TestData.invalidSingleAccount)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            assertNull(body.result)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 3: The rows are ordered by time in descending order,
    // and the offset skips as many of them as it is given.
    @Test
    fun getTransfersByTypeTo_case3() = runTest {
        val limit = 2
        val firstPage = SteemWorldClient.apiService.getTransfersByTypeTo(
            TestData.singleAccount,
            SteemWorldService.DEFAULT_OFFSET,
            limit
        ).body()?.result?.toTransferList() ?: listOf()
        val secondPage = SteemWorldClient.apiService.getTransfersByTypeTo(
            TestData.singleAccount,
            limit,
            limit
        ).body()?.result?.toTransferList() ?: listOf()

        assertTrue(firstPage.size <= limit)
        assertTrue(secondPage.size <= limit)
        for (i in 1 until firstPage.size) {
            assertTrue(firstPage[i - 1].time >= firstPage[i].time)
        }
        if (firstPage.size == limit && secondPage.isNotEmpty()) {
            assertNotEquals(firstPage[0], secondPage[0])
            assertTrue(firstPage.last().time >= secondPage[0].time)
        }
    }

    // Test case 1: Valid account — response is successful, and every author reward
    // belongs to a post of the account.
    @Test
    fun getRewards_author_case1() = runTest {
        val response = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_AUTHOR,
            TestData.singleAccount
        )
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNull(body.error)
            assertNotNull(body.result)
            val rewards = body.result?.toAuthorRewardList() ?: listOf()
            for (reward in rewards) {
                assertEquals(RewardType.AUTHOR, reward.type)
                assertEquals(TestData.singleAccount, reward.author)
                assertTrue(reward.permlink.isNotEmpty())
                assertTrue(reward.sbd >= 0f)
                assertTrue(reward.steem >= 0f)
                assertTrue(reward.vests >= 0f)
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid account — response is successful, but the body contains an error.
    @Test
    fun getRewards_author_case2() = runTest {
        val response = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_AUTHOR,
            TestData.invalidSingleAccount
        )
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            assertNull(body.result)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 1: Valid account — response is successful, and every curation reward
    // is paid in VESTS for a post of another account.
    @Test
    fun getRewards_curation_case1() = runTest {
        val response = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_CURATION,
            TestData.singleAccount
        )
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNull(body.error)
            assertNotNull(body.result)
            val rewards = body.result?.toCurationRewardList() ?: listOf()
            for (reward in rewards) {
                assertEquals(RewardType.CURATION, reward.type)
                assertTrue(reward.author.isNotEmpty())
                assertTrue(reward.permlink.isNotEmpty())
                // Curation rewards are paid in VESTS only.
                assertTrue(reward.sbd == 0f)
                assertTrue(reward.steem == 0f)
                assertTrue(reward.vests > 0f)
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: rewards_api orders the rows by time in ascending order,
    // and the offset skips as many of them as it is given.
    @Test
    fun getRewards_curation_case2() = runTest {
        val limit = 2
        val firstPage = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_CURATION,
            TestData.singleAccount,
            offset = SteemWorldService.DEFAULT_OFFSET,
            limit = limit
        ).body()?.result?.toCurationRewardList() ?: listOf()
        val secondPage = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_CURATION,
            TestData.singleAccount,
            offset = limit,
            limit = limit
        ).body()?.result?.toCurationRewardList() ?: listOf()

        assertTrue(firstPage.size <= limit)
        assertTrue(secondPage.size <= limit)
        for (i in 1 until firstPage.size) {
            assertTrue(firstPage[i - 1].time <= firstPage[i].time)
        }
        if (firstPage.size == limit && secondPage.isNotEmpty()) {
            assertNotEquals(firstPage[0], secondPage[0])
            assertTrue(firstPage.last().time <= secondPage[0].time)
        }
    }

    // Test case 3: rewards_api rejects 0 as fromTime, so MIN_REWARD_TIME has to be used
    // to read the whole history.
    @Test
    fun getRewards_curation_case3() = runTest {
        val response = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_CURATION,
            TestData.singleAccount,
            fromTime = 0L
        )
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 4: The offset skips as many rows as it is given instead of restarting at the
    // row it points to, so consecutive pages join without carrying a row twice.
    @Test
    fun getRewards_curation_case4() = runTest {
        val limit = 5
        suspend fun readPage(offset: Int, pageLimit: Int) = SteemWorldClient.apiService.getRewards(
            SteemWorldService.REWARD_OP_CURATION,
            TestData.singleAccount2,
            offset = offset,
            limit = pageLimit
        ).body()?.result?.toCurationRewardList() ?: listOf()

        val firstPage = readPage(SteemWorldService.DEFAULT_OFFSET, limit)
        val secondPage = readPage(limit, limit)
        val bothPagesAtOnce = readPage(SteemWorldService.DEFAULT_OFFSET, limit * 2)

        assertEquals(limit, firstPage.size)
        assertEquals(limit, secondPage.size)
        assertEquals(firstPage + secondPage, bothPagesAtOnce)
    }

    // Test case 1: Valid link — response is successful, rows are non-empty, and each row has
    // the correct format: [time, json_metadata, title, body]. json_metadata and title are the
    // only ones that may be empty, since an edit carries only the fields it changed.
    @Test
    fun getContentHistory_case1() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val response = SteemWorldClient.apiService.getContentHistory(author, permlink)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertNull(body.error)
            assertNotNull(body.result)
            val rows = body.result?.rows ?: listOf()
            assertTrue(rows.isNotEmpty())
            for (row in rows) {
                assertEquals(4, row.size)
                assertTrue(row[0] is Double)   // time (Unix timestamp)
                assertTrue(row[1] is String)   // json_metadata (a raw JSON string, not an object)
                assertTrue(row[2] is String)   // title
                assertTrue(row[3] is String)   // body
                assertTrue((row[3] as String).isNotEmpty())
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Invalid link — response is successful, but the body contains an error.
    @Test
    fun getContentHistory_case2() = runTest {
        val (author, permlink) = TestData.invalidContentLink
        val response = SteemWorldClient.apiService.getContentHistory(author, permlink)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            assertNull(body.result)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 3: An existing author with a permlink that does not exist is reported exactly
    // like an author that does not exist, so the two cannot be told apart from the response.
    @Test
    fun getContentHistory_case3() = runTest {
        val (author, permlink) = TestData.invalidPermlinkContentLink
        val response = SteemWorldClient.apiService.getContentHistory(author, permlink)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertNotEquals(0, body.code ?: 0)
            assertNotNull(body.error)
            assertNull(body.result)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 4: content_history_api orders the rows by time in ascending order and has no
    // parameter to change it, which is what the repository's reversal relies on.
    @Test
    fun getContentHistory_case4() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val revisions = SteemWorldClient.apiService.getContentHistory(author, permlink)
            .body()?.result?.toContentHistoryList() ?: listOf()

        assertTrue(revisions.size > 1)
        for (i in 1 until revisions.size) {
            assertTrue(revisions[i - 1].time <= revisions[i].time)
        }
    }

    // Test case 5: An edit carries only the fields it changed, so a revision can come back with
    // an empty title while the post still has one. The empty ones are filled in from the latest
    // earlier revision that did carry a title, which makes every mapped revision a whole post.
    @Test
    fun getContentHistory_case5() = runTest {
        val (author, permlink) = TestData.multipleRevisionsContentLink
        val result = SteemWorldClient.apiService.getContentHistory(author, permlink).body()?.result
        val rows = result?.rows ?: listOf()
        val revisions = result?.toContentHistoryList() ?: listOf()

        // The post this reads is there for the empty titles; without one the test proves nothing.
        assertTrue(rows.any { row -> (row[2] as? String).isNullOrEmpty() })
        for (revision in revisions) {
            assertTrue(revision.title.isNotEmpty())
        }
    }

    // Test case 6: A post that has never been edited has exactly one revision, and that
    // revision is the post as it was published.
    @Test
    fun getContentHistory_case6() = runTest {
        val (author, permlink) = TestData.singleRevisionContentLink
        val response = SteemWorldClient.apiService.getContentHistory(author, permlink)
        assertTrue(response.isSuccessful)
        response.body()?.let { body ->
            assertEquals(0, body.code ?: -1)
            assertEquals(1, body.result?.rows?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

}
