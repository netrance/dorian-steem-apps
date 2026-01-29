package lee.dorian.steem_ui.ui.history

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.usecase.ReadAccountHistoryUseCase
import lee.dorian.steem_domain.usecase.ReadDynamicGlobalPropertiesUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Test

import org.junit.Assert
import org.junit.Assert.*

class AccountHistoryViewModelTest : CommonPartOfViewModelTest() {

    private val steemRepository = SteemRepositoryImpl(dispatcher)
    private val accountHistoryViewModel = AccountHistoryViewModel(
        ReadAccountHistoryUseCase(
            steemRepository,
            dispatcher
        ),
        ReadDynamicGlobalPropertiesUseCase(
            steemRepository,
            dispatcher
        )
    )

    @Test
    fun readAccountHistory() = runTest {
        accountHistoryViewModel.readAccountHistory(TestData.singleAccount2)
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<AccountHistory> = accountHistoryViewModel.flowAccountHistoryState.value
        if (state !is State.Success<AccountHistory>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.historyList.size, ReadAccountHistoryUseCase.DEFAULT_LIMIT + 1)
        }
    }

    @Test
    fun appendAccountHistory() = runTest {
        accountHistoryViewModel.readAccountHistory(TestData.singleAccount2)
        Thread.sleep(WAITING_TIME_MSEC)

        accountHistoryViewModel.appendAccountHistory()
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<AccountHistory> = accountHistoryViewModel.flowAccountHistoryState.value
        if (state !is State.Success<AccountHistory>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.historyList.size, ReadAccountHistoryUseCase.DEFAULT_LIMIT * 2 + 1)
        }
    }

}