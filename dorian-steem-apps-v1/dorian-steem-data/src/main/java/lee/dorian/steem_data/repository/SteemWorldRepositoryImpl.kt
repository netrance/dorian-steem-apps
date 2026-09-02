package lee.dorian.steem_data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.reward.RewardsResultDTO
import lee.dorian.steem_data.model.transfer.GetTransfersResponseDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_data.retrofit.SteemWorldClient
import lee.dorian.steem_data.retrofit.SteemWorldService
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.Reward
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

    override suspend fun readAuthorRewards(
        account: String,
        fromTime: Long,
        toTime: Long
    ): ApiResult<List<Reward>> = readRewards(
        account, SteemWorldService.REWARD_OP_AUTHOR, fromTime, toTime
    ) { result, dgp ->
        result.toAuthorRewards(dgp)
    }

    override suspend fun readCurationRewards(
        account: String,
        fromTime: Long,
        toTime: Long
    ): ApiResult<List<Reward>> = readRewards(
        account, SteemWorldService.REWARD_OP_CURATION, fromTime, toTime
    ) { result, dgp ->
        result.toCurationRewards(dgp)
    }

    // rewards_api orders the rows by time in ascending order, has no parameter to change it,
    // and cuts the rows that exceed the limit off the newest end of the time range. The newest
    // rewards can therefore only be reported first once the whole time range has been read,
    // which is what this does: it reads page after page until one of them is not full.
    // Every op declares its own set of columns, so the caller passes the mapping function
    // that matches the op it asked for.
    private suspend fun readRewards(
        account: String,
        op: String,
        fromTime: Long,
        toTime: Long,
        toRewards: (RewardsResultDTO, GetDynamicGlobalPropertiesDTO) -> List<Reward>
    ): ApiResult<List<Reward>> = withContext(dispatcher) {
        val dgpParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        try {
            // Most rewards are paid out in VESTS, so the global properties needed to convert
            // them into Steem Power are read once, in parallel with the first page.
            val responseDGPAsync = async {
                SteemClient.apiService.getDynamicGlobalProperties(dgpParams)
            }
            val firstPageAsync = async {
                SteemWorldClient.apiService.getRewards(
                    op, account, fromTime, toTime, FIRST_REWARDS_OFFSET, REWARDS_PAGE_SIZE
                )
            }
            val responseDGP = responseDGPAsync.await()

            if (!responseDGP.isSuccessful) {
                return@withContext ApiResult.Failure(responseDGP.errorBody()?.string() ?: "")
            }
            val dgp = responseDGP.body()?.result
                ?: return@withContext ApiResult.Failure("Failed to read dynamic global properties")

            val rewards = mutableListOf<Reward>()
            var response = firstPageAsync.await()
            var offset = FIRST_REWARDS_OFFSET

            while (true) {
                if (!response.isSuccessful) {
                    return@withContext ApiResult.Failure(response.errorBody()?.string() ?: "")
                }

                // SDS reports its own errors (e.g. a non-existent account) with HTTP 200 and a
                // non-zero code, so the body has to be checked even when the response is successful.
                val body = response.body()
                    ?: return@withContext ApiResult.Failure("The body of response is empty")
                if (body.code != SDS_SUCCESS_CODE) {
                    return@withContext ApiResult.Failure(body.error ?: "")
                }

                val result = body.result ?: break
                val rowCount = result.rows?.size ?: 0
                rewards.addAll(toRewards(result, dgp))

                // A time range that holds more rewards than the app keeps is cut off at its
                // oldest end, so that the newest rewards of the range always survive.
                if (rewards.size > MAX_REWARD_COUNT) {
                    rewards.subList(0, rewards.size - MAX_REWARD_COUNT).clear()
                }

                // A page that is not full is the last one of the time range.
                if (rowCount < REWARDS_PAGE_SIZE) {
                    break
                }

                offset += REWARDS_PAGE_SIZE
                response = SteemWorldClient.apiService.getRewards(
                    op, account, fromTime, toTime, offset, REWARDS_PAGE_SIZE
                )
            }

            ApiResult.Success(rewards.reversed())
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

        // The most rewards a single time range is read into. A result of exactly this size
        // means the range was cut off, and the caller should narrow it down.
        const val MAX_REWARD_COUNT = 10000

        const val FIRST_REWARDS_OFFSET = SteemWorldService.DEFAULT_OFFSET
        const val REWARDS_PAGE_SIZE = SteemWorldService.MAX_REWARDS_LIMIT
    }

}
