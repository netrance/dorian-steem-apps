package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import org.junit.Assert
import org.junit.Test

class ReadPostAndRepliesUseCaseTest {

    val author = "dorian-lee"
    val authorInvalid = "dorian-invalid"
    val permlink = "1000"
    val readPostAndRepliesUseCase = ReadPostAndRepliesUseCase(SteemRepositoryImpl())

    // Test case 1: Read a post of @dorian-lee account.
    @Test
    fun invoke_case1() = runTest {
        val apiResult = readPostAndRepliesUseCase(author, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)
        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.isNotEmpty())
            Assert.assertTrue(data[0].account == author)
            Assert.assertTrue(data[0].permlink == permlink)
        }
    }

    // Test case 2: Read a post of an invalid account.
    @Test
    fun invoke_case2() = runTest {
        val apiResult = readPostAndRepliesUseCase(authorInvalid, permlink)
        Assert.assertTrue(apiResult is ApiResult.Success)
        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.isEmpty())
        }
    }

}