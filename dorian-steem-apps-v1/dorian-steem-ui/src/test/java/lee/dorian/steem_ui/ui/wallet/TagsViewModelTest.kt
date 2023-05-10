package lee.dorian.steem_ui.ui.wallet

import lee.dorian.steem_ui.ui.tags.TagsViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ui.tags.TagsState
import org.junit.Assert.assertTrue

class TagsViewModelTest : CommonPartOfViewModelTest() {

    private val tagViewModel = TagsViewModel()

    @Test
    fun readRankedPosts() {
        tagViewModel.apply {
            updateTag("kr")
            updateSort(R.id.radiobtn_trending)
            readRankedPosts()
        }
        Thread.sleep(3000)

        val tagsState = tagViewModel.flowTagsState.value
        assertTrue(tagsState is TagsState.Success)
        val tags = (tagsState as TagsState.Success).tagList
        assertEquals(tags.size, tagViewModel.limit)
    }

    @Test
    fun appendRankedPosts() {
        tagViewModel.apply {
            updateTag("kr")
            updateSort(R.id.radiobtn_trending)
            readRankedPosts()
        }
        Thread.sleep(3000)
        tagViewModel.appendRankedPosts("kr")
        Thread.sleep(3000)

        val tagsState = tagViewModel.flowTagsState.value
        assertTrue(tagsState is TagsState.Success)
        val tags = (tagsState as TagsState.Success).tagList
        assertEquals(tags.size, 2 * tagViewModel.limit)
    }

}