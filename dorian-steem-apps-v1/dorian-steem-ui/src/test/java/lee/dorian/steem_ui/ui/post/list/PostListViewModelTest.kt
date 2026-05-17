package lee.dorian.steem_ui.ui.post.list

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_domain.usecase.ReadPostsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Test

import org.junit.Assert.*

class PostListViewModelTest : CommonPartOfViewModelTest() {

    private val postListViewModel = PostListViewModel(
        SavedStateHandle().apply {
            set("account", TestData.singleAccount2)
            set("sort", "posts")
        },
        ReadPostsUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    @Test
    fun readPosts() = runBlocking {
        postListViewModel.readPosts()
        delay(WAITING_TIME_MSEC)

        val state: State<PostListInfo> = postListViewModel.flowState.value
        assertTrue("Expected Success but was $state", state is State.Success<PostListInfo>)
        assertEquals((state as State.Success).data.posts.size, postListViewModel.limit)
    }

    @Test
    fun appendRankedPosts() = runBlocking {
        postListViewModel.readPosts()
        delay(WAITING_TIME_MSEC)

        postListViewModel.appendPosts()
        delay(WAITING_TIME_MSEC)

        val state: State<PostListInfo> = postListViewModel.flowState.value
        assertTrue("Expected Success but was $state", state is State.Success<PostListInfo>)
        assertEquals((state as State.Success).data.posts.size, postListViewModel.limit * 2)
    }

}