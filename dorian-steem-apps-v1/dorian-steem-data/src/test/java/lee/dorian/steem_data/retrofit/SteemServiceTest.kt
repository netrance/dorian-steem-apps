package lee.dorian.steem_data.retrofit

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import org.junit.Test

import org.junit.Assert.*

class SteemServiceTest {

    // Test case 1: Trying to get the information of a valid account.
    @Test
    fun getAccounts_case1() = runTest {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(TestData.singleAccount)),
            id = 1
        )

        val response = SteemClient.apiService.getAccounts(getAccountParams)
        assertTrue(response.isSuccessful)
        response.body()?.let {
            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull("", it.result)
            assertEquals(1, it.result?.size ?: 0)
            assertEquals(TestData.singleAccount, it.result?.get(0)?.name ?: "")
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 2: Trying to get the information of an invalid account.
    @Test
    fun getAccounts_case2() = runTest {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(TestData.invalidSingleAccount)),
            id = 1
        )

        val response = SteemClient.apiService.getAccounts(getAccountParams)
        assertTrue(response.isSuccessful)
        response.body()?.let {
            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull("", it.result)
            assertEquals(0, it.result?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    // Test case 3: Trying to get the information of multiple valid accounts.
    @Test
    fun getAccounts_case3() = runTest {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(TestData.multipleAccounts),
            id = 1
        )

        val response = SteemClient.apiService.getAccounts(getAccountParams)
        assertTrue(response.isSuccessful)

        if (null == response.body()) {
            fail("The body of response is empty!")
            return@runTest
        }

        response.body()?.let {
            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull("", it.result)
            assertEquals(2, it.result?.size ?: 0)
        }

        if (null == response.body()) {
            fail("No account list from response!")
            return@runTest
        }

        response.body()?.result?.let { accountList ->
            for (account in TestData.multipleAccounts) {
                val filteredList = accountList.filter { it.name == account }
                assertTrue(filteredList.isNotEmpty())
            }
        }
    }

    // Test case 4: Trying to get the information of multiple invalid accounts.
    @Test
    fun getAccounts_case4() = runTest {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(TestData.multipleInvalidAccounts),
            id = 1
        )

        val response = SteemClient.apiService.getAccounts(getAccountParams)
        assertTrue(response.isSuccessful)
        response.body()?.let {
            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull("", it.result)
            assertEquals(0, it.result?.size ?: 0)
            return@runTest
        }

        fail("The body of response is empty!")
    }

    @Test
    fun getDynamicGlobalProperties() = runTest {
        val params = GetDynamicGlobalPropertiesParamsDTO(
            params = arrayOf(),
            id = 1
        )

        val response = SteemClient.apiService.getDynamicGlobalProperties(params)
        assertTrue(response.isSuccessful)
        response.body()?.let {
            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull(it.result)
            assertNotNull(it.result?.current_witness)
            assertTrue(it.result?.current_witness!!.isNotEmpty())
            return@runTest
        }

        fail("The body of response is empty!")
    }

    @Test
    fun getRankedPost() = runTest {
        val params = GetRankedPostParamsDTO(
            params = GetRankedPostParamsDTO.InnerParams(
                sort = GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
                tag = "kr",
                limit = 30
            ),
            id = 1
        )

        val response = SteemClient.apiService.getRankedPosts(params)
        assertTrue(response.isSuccessful)
        response.body()?.let {
            val postItemDTOList = it.result ?: listOf()

            assertEquals("2.0", it.jsonrpc ?: "")
            assertNotNull(it.result)
            assertTrue(postItemDTOList.size == 30)
            for (postItemDTO in postItemDTOList) {
                assertNotNull(postItemDTO.author)
                assertTrue((postItemDTO.author ?: "").isNotEmpty())
                assertNotNull(postItemDTO.title)
                assertTrue((postItemDTO.title ?: "").isNotEmpty())
            }
            return@runTest
        }

        fail("The body of response is empty!")
    }

}