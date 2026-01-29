package lee.dorian.steem_ui.ui.account_details

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_domain.usecase.ReadAccountDetailsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Assert.*
import org.junit.Test

class AccountDetailsViewModelTest : CommonPartOfViewModelTest() {

    private val postViewModel = AccountDetailsViewModel(ReadAccountDetailsUseCase(SteemRepositoryImpl(dispatcher), dispatcher))

    @Test
    fun readAccountDetails() = runTest {
        postViewModel.readAccountDetails(TestData.singleAccount)
        Thread.sleep(WAITING_TIME_MSEC)

        val state = postViewModel.accontDetailsState.value
        assertTrue(state is State.Success<AccountDetails>)
        val profile = (state as State.Success<AccountDetails>).data
        assertEquals(profile.name, TestData.singleAccount)
    }

}