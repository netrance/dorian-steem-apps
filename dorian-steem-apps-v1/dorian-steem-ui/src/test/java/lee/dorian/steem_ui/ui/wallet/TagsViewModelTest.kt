package lee.dorian.steem_ui.ui.wallet

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_ui.ui.tags.TagsViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ui.tags.TagsState
import org.junit.Assert.assertTrue

class TagsViewModelTest : CommonPartOfViewModelTest() {

    private val tagViewModel = TagsViewModel()

    @Test
    fun readRankedPosts() = runTest {
        tagViewModel.apply {
            tag = "kr"
            sort = "trending"
            readRankedPosts()
        }
        Thread.sleep(3000)

        val tagsState = tagViewModel.flowTagsState.value
        assertTrue(tagsState is TagsState.Success)
        val tags = (tagsState as TagsState.Success).tagList
        assertEquals(tags.size, tagViewModel.limit)
    }

    @Test
    fun appendRankedPosts() = runTest {
        tagViewModel.apply {
            tag = "kr"
            sort = "trending"
            readRankedPosts()
        }
        Thread.sleep(3000)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is TagsState.Success)
            val tags = (newState as TagsState.Success).tagList
            assertEquals(tags.size, tagViewModel.limit)
        }

        tagViewModel.appendRankedPosts()
        Thread.sleep(3000)
        tagViewModel.flowTagsState.value.also { newState ->
            assertTrue(newState is TagsState.Success)
            val tags = (newState as TagsState.Success).tagList
            assertEquals(tags.size, 2 * tagViewModel.limit)
        }
    }

}