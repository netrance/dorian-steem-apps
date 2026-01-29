package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadAccountHistoryUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {
        const val DEFAULT_LIMIT = 20
    }

    operator suspend fun invoke(
        account: String,
        limit: Int = DEFAULT_LIMIT,
        existingList: List<AccountHistoryItem> = listOf()
    ): ApiResult<List<AccountHistoryItem>> = withContext(dispatcher) {
        val apiResult = try {
            steemRepository.readAccountHistory(
                account,
                limit,
                existingList
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }

        return@withContext apiResult
    }
}