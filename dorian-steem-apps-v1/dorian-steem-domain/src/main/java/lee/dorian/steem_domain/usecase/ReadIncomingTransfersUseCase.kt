package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_domain.repository.SteemWorldRepository
import javax.inject.Inject

class ReadIncomingTransfersUseCase @Inject constructor(
    private val steemWorldRepository: SteemWorldRepository,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {
        const val DEFAULT_LIMIT = 100
        const val DEFAULT_OFFSET = 0
    }

    suspend operator fun invoke(
        account: String,
        offset: Int = DEFAULT_OFFSET,
        limit: Int = DEFAULT_LIMIT
    ): ApiResult<List<Transfer>> = withContext(dispatcher) {
        try {
            steemWorldRepository.readIncomingTransfers(account, offset, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
