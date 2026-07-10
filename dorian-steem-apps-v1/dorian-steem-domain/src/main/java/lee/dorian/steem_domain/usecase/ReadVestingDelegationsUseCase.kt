package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadVestingDelegationsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val DEFAULT_LIMIT = 100
    }

    suspend operator fun invoke(
        account: String,
        startAccount: String = "",
        limit: Int = DEFAULT_LIMIT
    ): ApiResult<List<VestingDelegation>> = withContext(dispatcher) {
        try {
            steemRepository.readVestingDelegations(account, startAccount, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
