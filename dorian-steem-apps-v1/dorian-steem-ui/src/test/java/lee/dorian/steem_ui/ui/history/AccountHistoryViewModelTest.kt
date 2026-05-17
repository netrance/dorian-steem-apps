package lee.dorian.steem_ui.ui.history

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.usecase.ReadAccountHistoryUseCase
import lee.dorian.steem_domain.usecase.ReadDynamicGlobalPropertiesUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Test

import org.junit.Assert.*

class AccountHistoryViewModelTest : CommonPartOfViewModelTest() {

    private val steemRepository = SteemRepositoryImpl(dispatcher)
    private val accountHistoryViewModel = AccountHistoryViewModel(
        SavedStateHandle().apply {
            set("account", TestData.singleAccount2)
        },
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
    fun readAccountHistory() = runBlocking {
        accountHistoryViewModel.readAccountHistory()
        delay(WAITING_TIME_MSEC)

        val state: State<AccountHistory> = accountHistoryViewModel.flowAccountHistoryState.value
        assertTrue("Expected Success but was $state", state is State.Success<AccountHistory>)
        assertEquals((state as State.Success).data.historyList.size, ReadAccountHistoryUseCase.DEFAULT_LIMIT + 1)
    }

    @Test
    fun appendAccountHistory() = runBlocking {
        accountHistoryViewModel.readAccountHistory()
        delay(WAITING_TIME_MSEC)

        accountHistoryViewModel.appendAccountHistory()
        delay(WAITING_TIME_MSEC)

        val state: State<AccountHistory> = accountHistoryViewModel.flowAccountHistoryState.value
        assertTrue("Expected Success but was $state", state is State.Success<AccountHistory>)
        assertEquals((state as State.Success).data.historyList.size, ReadAccountHistoryUseCase.DEFAULT_LIMIT * 2 + 1)
    }

}