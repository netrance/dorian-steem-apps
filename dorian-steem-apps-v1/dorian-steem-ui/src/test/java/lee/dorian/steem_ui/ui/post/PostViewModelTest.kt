package lee.dorian.steem_ui.ui.post

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.ui.post.content.PostState
import lee.dorian.steem_ui.ui.post.content.PostViewModel
import org.junit.Assert.*
import org.junit.Test

class PostViewModelTest : CommonPartOfViewModelTest() {

    private val postViewModel = PostViewModel(ReadPostAndRepliesUseCase(SteemRepositoryImpl(), dispatcher))

    private val author = "dorian-lee"
    private val permlink = "1000"

    @Test
    fun readPostAndReplies() = runTest {
        postViewModel.readPostAndReplies(author, permlink)
        Thread.sleep(WAITING_TIME_MSEC)

        val state = postViewModel.flowPostState.value
        assertTrue(state is PostState.Success)
        (state as PostState.Success).also {
            val post = it.post
            val replies = it.replies
            assertEquals(author, post.account)
            assertTrue(replies.size > 0)
        }
    }

}