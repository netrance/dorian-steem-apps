package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.model.history.GetAccountHistoryParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadAccountHistoryUseCaseTest : CommonPartOfViewModelTest() {

    val readAccountHistoryUseCase = ReadAccountHistoryUseCase(
        SteemRepositoryImpl(dispatcher),
        dispatcher
    )

    private fun testAccountHistoryList(historyItemList: List<AccountHistoryItem>) {
        for (accountHistoryItem in historyItemList) {
            Assert.assertTrue(accountHistoryItem.index > 0)
            Assert.assertTrue(accountHistoryItem.localTime.isNotEmpty())
            Assert.assertTrue(accountHistoryItem.content.isNotEmpty())
        }
    }

    @Test
    fun invoke_case1() = runTest {
        val apiResult = readAccountHistoryUseCase(
            TestData.singleAccount
        )
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertEquals(data.size, GetAccountHistoryParamsDTO.InnerParams.DEFAULT_LIMIT + 1)
            testAccountHistoryList(data)
        }

        val existingList = apiResult.data
        val apiResult2 = readAccountHistoryUseCase(
            TestData.singleAccount,
            existingList = existingList
        )

        with(apiResult2 as ApiResult.Success) {
            Assert.assertEquals(data.size, GetAccountHistoryParamsDTO.InnerParams.DEFAULT_LIMIT + 1)
            testAccountHistoryList(data)
        }
    }

}