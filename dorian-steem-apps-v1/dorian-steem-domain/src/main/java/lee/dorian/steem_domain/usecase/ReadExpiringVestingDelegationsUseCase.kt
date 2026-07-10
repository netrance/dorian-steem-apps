package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadExpiringVestingDelegationsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val AFTER_EPOCH_START = "1970-01-01T00:00:00"
    }

    suspend operator fun invoke(
        account: String,
        after: String = AFTER_EPOCH_START
    ): ApiResult<List<ExpiringVestingDelegation>> = withContext(dispatcher) {
        try {
            steemRepository.readExpiringVestingDelegations(account, after)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
