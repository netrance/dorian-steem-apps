package lee.dorian.steem_domain.usecase

import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.PostItem
import org.junit.Assert
import org.junit.Test

class ReadRankedPostsUseCaseTest {

    val readRankedPostsUseCase = ReadRankedPostsUseCase(SteemRepositoryImpl())

    private fun testPostItemList(postItemList: List<PostItem>) {
        for (postItem in postItemList) {
            Assert.assertTrue(postItem.account.isNotEmpty())
            Assert.assertTrue(postItem.permlink.isNotEmpty())
            Assert.assertTrue(postItem.title.isNotEmpty())
            Assert.assertTrue(postItem.content.isNotEmpty())
        }
    }

    // Test case 1: Read the first 20 trending posts containing "kr" tag.
    @Test
    fun invoke_case1() {
        readRankedPostsUseCase(
            GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
            "kr"
        ).subscribe { postItemList ->
            Assert.assertEquals(postItemList.size, GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT)
            testPostItemList(postItemList)
        }
    }

    // Test case 2: Read the first 20 trending posts and the second 20 ones containing "kr" tag.
    @Test
    fun invoke_case2() {
        var lastPostItem: PostItem? = null
        val postItemList = mutableListOf<PostItem>()

        readRankedPostsUseCase(
            GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
            "kr"
        ).subscribe { readPostItemList ->
            lastPostItem = readPostItemList.last()
            postItemList.addAll(readPostItemList)
            testPostItemList(postItemList)
        }

        readRankedPostsUseCase(
            GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
            "kr",
            lastPostItem = lastPostItem
        ).subscribe { readPostItemList ->
            postItemList.addAll(readPostItemList)
            Assert.assertEquals(readPostItemList.size, GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT)
            Assert.assertEquals(postItemList.size, 2 * GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT)
            testPostItemList(postItemList)
        }
    }

}