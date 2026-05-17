package lee.dorian.steem_ui.ui.tags

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.model.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TagsViewModelTest : CommonPartOfViewModelTest() {

    private val tagViewModel = TagsViewModel(
        ReadRankedPostsUseCase(SteemRepositoryImpl(dispatcher), dispatcher)
    )

    @Test
    fun readRankedPosts() = runBlocking {
        tagViewModel.apply {
            readRankedPosts("kr", "trending")
        }
        delay(WAITING_TIME_MSEC)

        val tagsState = tagViewModel.flowTagsState.value
        assertTrue(tagsState is State.Success)
        val tags = (tagsState as State.Success).data
        assertEquals(tags.size, tagViewModel.limit)
    }

    @Test
    fun appendRankedPosts() = runBlocking {
        tagViewModel.apply {
            readRankedPosts("kr", "trending")
        }
        delay(WAITING_TIME_MSEC)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is State.Success)
            val tags = (newState as State.Success).data
            assertEquals(tags.size, tagViewModel.limit)
        }

        tagViewModel.appendRankedPosts("kr", "trending")
        delay(WAITING_TIME_MSEC)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is State.Success)
            val tags = (newState as State.Success).data
            assertEquals(tags.size, 2 * tagViewModel.limit)
        }
    }

}
