package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.repository.SteemRepository

class ReadRankedPostsUseCase(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator suspend fun invoke(
        sort: String,
        tag: String,
        observer: String = "",
        limit: Int = 20,
        existingList: List<PostItem> = listOf()
    ): ApiResult<List<PostItem>> = withContext(dispatcher) {
        val apiResult = try {
            steemRepository.readRankedPosts(
                sort,
                tag,
                observer,
                limit,
                existingList
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }

        apiResult
    }

}