package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.repository.SteemWorldRepository
import javax.inject.Inject

class ReadAuthorRewardsUseCase @Inject constructor(
    private val steemWorldRepository: SteemWorldRepository,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {
        // rewards_api rejects 0 as fromTime, so the whole history is read as MIN - MAX.
        const val DEFAULT_FROM_TIME = 1L
        const val DEFAULT_TO_TIME = 9999999999L

        // The most rewards a single time range is read into. A result of exactly this size
        // means the range was cut off at its oldest end, and has to be narrowed down to
        // be shown in full.
        const val MAX_REWARD_COUNT = 10000
    }

    // Reads every author reward paid to the account in the given time range, latest first.
    // rewards_api can only be read oldest first, so the whole range is read at once instead
    // of page by page. The range is the only thing that bounds the amount of work, which
    // makes it the caller's job to keep it small enough to be worth reading.
    suspend operator fun invoke(
        account: String,
        fromTime: Long = DEFAULT_FROM_TIME,
        toTime: Long = DEFAULT_TO_TIME
    ): ApiResult<List<Reward>> = withContext(dispatcher) {
        try {
            steemWorldRepository.readAuthorRewards(account, fromTime, toTime)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
