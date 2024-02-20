package lee.dorian.steem_ui.ui.post.blog

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.Blog
import lee.dorian.steem_domain.usecase.ReadPostsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import lee.dorian.steem_ui.model.State
import org.junit.Test

import lee.dorian.steem_ui.ui.tags.TagsState
import org.junit.Assert
import org.junit.Assert.*

class BlogViewModelTest : CommonPartOfViewModelTest() {

    private val blogViewModel = BlogViewModel(ReadPostsUseCase(SteemRepositoryImpl(), dispatcher))

    @Test
    fun readPosts() = runTest {
        blogViewModel.readPosts(TestData.singleAccount2)
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<Blog> = blogViewModel.flowState.value
        if (state !is State.Success<Blog>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.posts.size, blogViewModel.limit)
        }
    }

    @Test
    fun appendRankedPosts() = runTest {
        blogViewModel.readPosts(TestData.singleAccount2)
        Thread.sleep(WAITING_TIME_MSEC)

        blogViewModel.appendPosts()
        Thread.sleep(WAITING_TIME_MSEC)

        val state: State<Blog> = blogViewModel.flowState.value
        if (state !is State.Success<Blog>) {
            Assert.fail()
        }
        else {
            assertEquals(state.data.posts.size, blogViewModel.limit * 2)
        }
    }

}