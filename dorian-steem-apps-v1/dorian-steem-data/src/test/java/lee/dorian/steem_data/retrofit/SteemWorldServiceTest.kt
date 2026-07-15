package lee.dorian.steem_data.retrofit

import kotlinx.coroutines.test.runTest
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

}
