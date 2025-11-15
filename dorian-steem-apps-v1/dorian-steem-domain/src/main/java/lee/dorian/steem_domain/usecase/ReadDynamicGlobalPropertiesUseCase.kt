package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_domain.model.DynamicGlobalProperties
import lee.dorian.steem_domain.repository.SteemRepository

class ReadDynamicGlobalPropertiesUseCase(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(): ApiResult<DynamicGlobalProperties> = withContext(dispatcher) {
        val apiResult = try {
            steemRepository.readDynamicGlobalProperties()
        }
        catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }

        return@withContext apiResult
    }
}