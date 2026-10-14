package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.ui.post.content.PostContentState
import lee.dorian.steem_ui.ui.post.content.PostContentViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric is required, not a preference. The ViewModel reads its route with
// SavedStateHandle.toRoute(), and androidx.navigation's SavedStateArgStore puts the value into
// an android.os.Bundle and reads it back out again. This module stubs the Android framework in
// unit tests (unitTests.returnDefaultValues), so that Bundle swallows the value and the route
// decodes to null — "Unexpected null value for non-nullable argument author". A real Bundle is
// the only way around it. (see docs/TESTING.md)
@RunWith(RobolectricTestRunner::class)
class PostContentViewModelTest : CommonPartOfViewModelTest() {

    private val savedStateHandle = SavedStateHandle().apply {
        set("author", "dorian-lee")
        set("permlink", "1000")
    }

    private val postContentViewModel = PostContentViewModel(
        savedStateHandle,
        ReadPostAndRepliesUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    // Every test body has to end in a Unit expression. `fun x() = runBlocking { ... }` takes its
    // return type from the last expression in the block, and JUnit4 rejects a test method that
    // returns anything else with "Method x() should be void" — which invalidates the whole class,
    // not just that one method.
    @Test
    fun readPostAndReplies() = runBlocking {
        postContentViewModel.readPostAndReplies()
        delay(WAITING_TIME_MSEC)

        val state = postContentViewModel.flowPostState.value
        assertTrue(state is PostContentState.Success)

        val post = (state as PostContentState.Success).post
        val replies = state.replies
        assertEquals(savedStateHandle["author"], post.account)
        assertTrue(replies.size > 0)
    }

}
