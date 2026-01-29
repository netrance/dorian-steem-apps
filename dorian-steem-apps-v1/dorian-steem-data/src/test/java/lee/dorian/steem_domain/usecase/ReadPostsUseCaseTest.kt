package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.model.post.GetAccountPostParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReadPostsUseCaseTest : CommonPartOfViewModelTest() {

    val readPostsUseCase = ReadPostsUseCase(
        SteemRepositoryImpl(dispatcher),
        dispatcher
    )

    private fun testPostItemList(postItemList: List<PostItem>) {
        for (postItem in postItemList) {
            Assert.assertTrue(postItem.account.isNotEmpty())
            Assert.assertTrue(postItem.permlink.isNotEmpty())
            Assert.assertTrue(postItem.title.isNotEmpty())
            Assert.assertTrue(postItem.content.isNotEmpty())
        }
    }

    // Test case 1: Read the first 20 trending posts from a blog.
    @Test
    fun invoke_case1() = runTest {
        val apiResult = readPostsUseCase(
            TestData.singleAccount,
            GetAccountPostParamsDTO.InnerParams.SORT_BLOG
        )
        assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertEquals(data.size, GetAccountPostParamsDTO.InnerParams.DEFAULT_LIMIT)
            testPostItemList(data)
        }
    }

    // Test case 2: Read the first 20 posts and the second 20 ones from a blog.
    @Test
    fun invoke_case2() = runTest {
        var lastPostItem: PostItem? = null
        val postItemList = mutableListOf<PostItem>()

        val firstApiResult = readPostsUseCase(
            TestData.singleAccount2,
            GetAccountPostParamsDTO.InnerParams.SORT_BLOG
        )
        assertTrue(firstApiResult is ApiResult.Success)
        val firstPostItemList = (firstApiResult as ApiResult.Success).data
        lastPostItem = firstPostItemList.last()
        postItemList.addAll(firstPostItemList)
        testPostItemList(postItemList)

        val secondApiResult = readPostsUseCase(
            TestData.singleAccount2,
            GetAccountPostParamsDTO.InnerParams.SORT_BLOG,
            existingList = postItemList
        )
        assertTrue(secondApiResult is ApiResult.Success)
        val secondPostItemList = (secondApiResult as ApiResult.Success).data
        postItemList.addAll(secondPostItemList)
        assertEquals(GetAccountPostParamsDTO.InnerParams.DEFAULT_LIMIT * 2, postItemList.size)
        testPostItemList(postItemList)
    }

}