package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.HistoryItem
import lee.dorian.steem_domain.repository.SteemRepository

class ReadAccountHistoryUseCase(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    companion object {
        const val DEFAULT_LIMIT = 20
    }

    operator suspend fun invoke(
        account: String,
        limit: Int = DEFAULT_LIMIT,
        existingList: List<HistoryItem> = listOf()
    ): ApiResult<List<HistoryItem>> = withContext(dispatcher) {
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