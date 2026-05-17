package lee.dorian.steem_ui.ui.account_details

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_domain.usecase.ReadAccountDetailsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Assert.*
import org.junit.Test

class AccountDetailsViewModelTest : CommonPartOfViewModelTest() {

    private val accountDetailsViewModel = AccountDetailsViewModel(
        SavedStateHandle().apply {
            set("account", TestData.singleAccount)
        },
        ReadAccountDetailsUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    @Test
    fun readAccountDetails() = runBlocking {
        accountDetailsViewModel.readAccountDetails()
        delay(WAITING_TIME_MSEC)

        val state = accountDetailsViewModel.accountDetailsState.value
        assertTrue(state is State.Success<AccountDetails>)
        val profile = (state as State.Success<AccountDetails>).data
        assertEquals(profile.name, TestData.singleAccount)
    }

}