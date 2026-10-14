package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ContentHistory
import lee.dorian.steem_domain.repository.SteemWorldRepository
import javax.inject.Inject

class ReadContentHistoryUseCase @Inject constructor(
    private val steemWorldRepository: SteemWorldRepository,
    private val dispatcher: CoroutineDispatcher
) {

    // Reads every revision of the given post, latest first, so the first item is the post as it
    // reads now and the last one is the post as it was published. A post that has never been
    // edited has exactly one revision, which makes an empty list the sign of a post that does
    // not exist rather than of a post that was never edited.
    //
    // content_history_api takes no limit or offset, so the whole history always comes back at
    // once. The number of revisions is the author's doing and cannot be bounded here.
    suspend operator fun invoke(
        author: String,
        permlink: String
    ): ApiResult<List<ContentHistory>> = withContext(dispatcher) {
        try {
            steemWorldRepository.readContentHistory(author, permlink)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
