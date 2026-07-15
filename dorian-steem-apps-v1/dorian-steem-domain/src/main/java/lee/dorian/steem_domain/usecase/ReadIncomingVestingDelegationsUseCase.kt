package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.repository.SteemWorldRepository
import javax.inject.Inject

class ReadIncomingVestingDelegationsUseCase @Inject constructor(
    private val steemWorldRepository: SteemWorldRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        account: String
    ): ApiResult<List<VestingDelegation>> = withContext(dispatcher) {
        try {
            steemWorldRepository.readIncomingVestingDelegations(account)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
