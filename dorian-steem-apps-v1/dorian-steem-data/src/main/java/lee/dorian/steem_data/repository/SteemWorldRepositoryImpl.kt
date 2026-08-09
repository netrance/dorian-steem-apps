package lee.dorian.steem_data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.transfer.GetTransfersResponseDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_data.retrofit.SteemWorldClient
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_domain.model.VestingDelegation
import retrofit2.Response
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

    override suspend fun readOutgoingVestingDelegations(
        account: String
    ): ApiResult<List<VestingDelegation>> = withContext(dispatcher) {
        val dgpParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        try {
            val responseDGPAsync = async {
                SteemClient.apiService.getDynamicGlobalProperties(dgpParams)
            }
            val responseDelegationsAsync = async {
                SteemWorldClient.apiService.getOutgoingDelegations(account)
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

    override suspend fun readExpiringVestingDelegations(
        account: String
    ): ApiResult<List<ExpiringVestingDelegation>> = withContext(dispatcher) {
        val dgpParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        try {
            val responseDGPAsync = async {
                SteemClient.apiService.getDynamicGlobalProperties(dgpParams)
            }
            val responseDelegationsAsync = async {
                SteemWorldClient.apiService.getExpiringDelegations(account)
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
            val resultList = responseDelegations.body()?.result?.toExpiringVestingDelegations(dgp) ?: listOf()
            ApiResult.Success(resultList)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

    override suspend fun readOutgoingTransfers(
        account: String,
        offset: Int,
        limit: Int
    ): ApiResult<List<Transfer>> = withContext(dispatcher) {
        try {
            val response = SteemWorldClient.apiService.getTransfersByTypeFrom(account, offset, limit)
            response.toTransfersApiResult()
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

    override suspend fun readIncomingTransfers(
        account: String,
        offset: Int,
        limit: Int
    ): ApiResult<List<Transfer>> = withContext(dispatcher) {
        try {
            val response = SteemWorldClient.apiService.getTransfersByTypeTo(account, offset, limit)
            response.toTransfersApiResult()
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

    // SDS reports its own errors (e.g. a non-existent account) with HTTP 200 and a non-zero code,
    // so the body has to be checked even when the response itself is successful.
    private fun Response<GetTransfersResponseDTO>.toTransfersApiResult(): ApiResult<List<Transfer>> {
        if (!isSuccessful) {
            return ApiResult.Failure(errorBody()?.string() ?: "")
        }

        val body = body() ?: return ApiResult.Failure("The body of response is empty")
        if (body.code != SDS_SUCCESS_CODE) {
            return ApiResult.Failure(body.error ?: "")
        }

        return ApiResult.Success(body.result?.toTransfers() ?: listOf())
    }

    companion object {
        const val SDS_SUCCESS_CODE = 0
    }

}
