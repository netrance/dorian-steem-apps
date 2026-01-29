package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadPostAndRepliesUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    operator suspend fun invoke(
        author: String,
        permlink: String
    ): ApiResult<List<Post>> = withContext(dispatcher) {
        val apiResult = try {
            steemRepository.readPostAndReplies(
                author,
                permlink
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }

        apiResult
    }

}