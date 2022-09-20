package lee.dorian.steem_data.retrofit

import lee.dorian.steem_data.constants.TestData
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import org.junit.Test

import org.junit.Assert.*

class SteemServiceTest {

    // Test case 1: Trying to get the information of a valid account.
    @Test
    fun getAccounts_case1() {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(TestData.singleAccount)),
            id = 1
        )

        SteemClient.apiService.getAccounts(getAccountParams).subscribe { responseEntity ->
            assertEquals("2.0", responseEntity.jsonrpc ?: "")
            assertNotNull("", responseEntity.result)
            assertEquals(1, responseEntity.result?.size ?: 0)
            assertEquals(TestData.singleAccount, responseEntity.result[0].name)
        }
    }

    // Test case 2: Trying to get the information of an invalid account.
    @Test
    fun getAccounts_case2() {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(TestData.invalidSingleAccount)),
            id = 1
        )

        SteemClient.apiService.getAccounts(getAccountParams).subscribe { responseEntity ->
            assertEquals("2.0", responseEntity.jsonrpc)
            assertNotNull("", responseEntity.result)
            assertEquals(0, responseEntity.result?.size)
        }
    }

    // Test case 3: Trying to get the information of multiple valid accounts.
    @Test
    fun getAccounts_case3() {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(TestData.multipleAccounts),
            id = 1
        )

        SteemClient.apiService.getAccounts(getAccountParams).subscribe { responseEntity ->
            assertEquals("2.0", responseEntity.jsonrpc ?: "")
            assertNotNull("", responseEntity.result)
            assertEquals(2, responseEntity.result?.size ?: 0)

            responseEntity.result?.let {
                for (i in 0 .. it.size - 1) {
                    assertEquals(TestData.multipleAccounts[i], it[i].name)
                }
            }
        }
    }

    // Test case 4: Trying to get the information of multiple invalid accounts.
    @Test
    fun getAccounts_case4() {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(TestData.multipleInvalidAccounts),
            id = 1
        )

        SteemClient.apiService.getAccounts(getAccountParams).subscribe { responseEntity ->
            assertEquals("2.0", responseEntity.jsonrpc)
            assertNotNull("", responseEntity.result)
            assertEquals(0, responseEntity.result?.size)
        }
    }

}