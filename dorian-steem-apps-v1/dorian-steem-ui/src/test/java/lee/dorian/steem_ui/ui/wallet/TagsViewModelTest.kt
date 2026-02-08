package lee.dorian.steem_ui.ui.wallet

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.ui.tags.TagsViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.tags.TagsState
import org.junit.Assert.assertTrue

class TagsViewModelTest : CommonPartOfViewModelTest() {

    private val tagViewModel = TagsViewModel(
        ReadRankedPostsUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    @Test
    fun readRankedPosts() = runTest {
        tagViewModel.apply {
            readRankedPosts("kr", "trending")
        }
        Thread.sleep(3000)

        val tagsState = tagViewModel.flowTagsState.value
        assertTrue(tagsState is State.Success)
        val tags = (tagsState as State.Success).data
        assertEquals(tags.size, tagViewModel.limit)
    }

    @Test
    fun appendRankedPosts() = runTest {
        tagViewModel.apply {
            readRankedPosts("kr", "trending")
        }
        Thread.sleep(3000)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is State.Success)
            val tags = (newState as State.Success).data
            assertEquals(tags.size, tagViewModel.limit)
        }

        tagViewModel.appendRankedPosts("kr", "trending")
        Thread.sleep(3000)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is State.Success)
            val tags = (newState as State.Success).data
            assertEquals(tags.size, 2 * tagViewModel.limit)
        }
    }

}