package lee.dorian.steem_ui.ui.post.list

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_domain.usecase.ReadPostsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Test

import org.junit.Assert
import org.junit.Assert.*

class PostListViewModelTest : CommonPartOfViewModelTest() {

    private val postListViewModel = PostListViewModel(ReadPostsUseCase(SteemRepositoryImpl(dispatcher), dispatcher))

    @Test
    fun readPosts() = runTest {
        postListViewModel.readPosts(TestData.singleAccount2, "posts")
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<PostListInfo> = postListViewModel.flowState.value
        if (state !is State.Success<PostListInfo>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.posts.size, postListViewModel.limit)
        }
    }

    @Test
    fun appendRankedPosts() = runTest {
        postListViewModel.readPosts(TestData.singleAccount2, "posts")
        Thread.sleep(WAITING_TIME_MSEC)

        postListViewModel.appendPosts()
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<PostListInfo> = postListViewModel.flowState.value
        if (state !is State.Success<PostListInfo>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.posts.size, postListViewModel.limit * 2)
        }
    }

}