package lee.dorian.steem_data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_data.retrofit.SteemWorldClient
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.repository.SteemWorldRepository
import javax.inject.Inject

class SteemWorldRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) : SteemWorldRepository {

    override suspend fun readIncomingVestingDelegations(
        account: String
    ): ApiResult<List<VestingDelegation>> = withContext(dispatcher) {
        val dgpParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        try {
            val responseDGPAsync = async {
                SteemClient.apiService.getDynamicGlobalProperties(dgpParams)
            }
            val responseDelegationsAsync = async {
                SteemWorldClient.apiService.getIncomingDelegations(account)
            }
            val responseDGP = responseDGPAsync.await()
            val responseDelegations = responseDelegationsAsync.await()

            if (!responseDGP.isSuccessful) {
                return@withContext ApiResult.Failure(responseDGP.errorBody()?.string() ?: "")
            }
            if (!responseDelegations.isSuccessful) {
                return@withContext ApiResult.Failure(responseDelegations.errorBody()?.string() ?: "")
            }

            val dgp = responseDGP.body()?.result
                ?: return@withContext ApiResult.Failure("Failed to read dynamic global properties")
            val resultList = responseDelegations.body()?.result?.toVestingDelegations(dgp) ?: listOf()
            ApiResult.Success(resultList)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

}
