package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.ui.post.content.PostContentState
import lee.dorian.steem_ui.ui.post.content.PostContentViewModel
import org.junit.Assert.*
import org.junit.Test

class PostContentViewModelTest : CommonPartOfViewModelTest() {

    private val postContentViewModel = PostContentViewModel(
        SavedStateHandle(),
        ReadPostAndRepliesUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    private val author = "dorian-lee"
    private val permlink = "1000"

    @Test
    fun readPostAndReplies() = runTest {
        postContentViewModel.readPostAndReplies(author, permlink)
        Thread.sleep(WAITING_TIME_MSEC)

        val state = postContentViewModel.flowPostState.value
        assertTrue(state is PostContentState.Success)
        (state as PostContentState.Success).also {
            val post = it.post
            val replies = it.replies
            assertEquals(author, post.account)
            assertTrue(replies.size > 0)
        }
    }

}