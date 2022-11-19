package lee.dorian.steem_ui.ui.wallet

import lee.dorian.steem_ui.ui.tags.TagsViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TagsViewModelTest : CommonPartOfViewModelTest() {

    private val tagViewModel = TagsViewModel()

    @Test
    fun readRankedPosts() {
        tagViewModel.readRankedPosts(
            "trending",
            "kr"
        )
        Thread.sleep(3000)
        assertEquals(tagViewModel.rankedPosts.value?.size, tagViewModel.limit)
    }

    @Test
    fun appendRankedPosts() {
        tagViewModel.readRankedPosts(
            "created",
            "kr"
        )
        Thread.sleep(3000)
        tagViewModel.appendRankedPosts()
        Thread.sleep(3000)
        assertEquals(tagViewModel.rankedPosts.value?.size, 2 * tagViewModel.limit)
    }

}