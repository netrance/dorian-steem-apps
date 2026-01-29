package lee.dorian.steem_ui.ui.profile

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.usecase.ReadSteemitProfileUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Assert.*
import org.junit.Test

class ProfileViewModelTest : CommonPartOfViewModelTest() {

    private val postViewModel = ProfileViewModel(State.Empty, ReadSteemitProfileUseCase(SteemRepositoryImpl(dispatcher), dispatcher))

    @Test
    fun readSteemitProfile() = runTest {
        postViewModel.readSteemitProfile(TestData.singleAccount)
        Thread.sleep(WAITING_TIME_MSEC)

        val state = postViewModel.profileState.value
        assertTrue(state is State.Success<SteemitProfile>)
        val profile = (state as State.Success<SteemitProfile>).data
        assertTrue(profile is SteemitProfile)
        assertEquals(profile.account, TestData.singleAccount)
        assertTrue(profile.followingCount > 0)
        assertTrue(profile.followerCount > 0)
    }

}