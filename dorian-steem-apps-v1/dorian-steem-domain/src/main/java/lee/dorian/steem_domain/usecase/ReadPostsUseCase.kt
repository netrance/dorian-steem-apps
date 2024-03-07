package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.repository.SteemRepository

class ReadPostsUseCase(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator suspend fun invoke(
        account: String,
        sort: String,
        observer: String = "",
        limit: Int = 20,
        existingList: List<PostItem> = mutableListOf()
    ): ApiResult<List<PostItem>> = withContext(dispatcher) {
        val apiResult = try {
            steemRepository.readPosts(
                account,
                sort,
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