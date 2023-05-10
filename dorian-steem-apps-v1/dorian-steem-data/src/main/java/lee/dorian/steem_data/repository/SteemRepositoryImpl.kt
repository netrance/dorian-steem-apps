package lee.dorian.steem_data.repository

import kotlinx.coroutines.*
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class SteemRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SteemRepository {

    override suspend fun readSteemitWallet(
        account: String
    ): ApiResult<Array<SteemitWallet>> = withContext(dispatcher) {
        val getDynamicGlobalPropertiesParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        try {
            val responseDGP = SteemClient.apiService.getDynamicGlobalProperties(
                getDynamicGlobalPropertiesParams
            )
            if (!responseDGP.isSuccessful) {
                ApiResult.Failure(responseDGP.errorBody()?.string() ?: "")
            }

            val nullableDGP = responseDGP.body()?.result
            if (null == nullableDGP) {
                ApiResult.Failure("Failed to read dynamic global properties" ?: "")
            }

            val dgp = nullableDGP!!
            val responseAccounts = SteemClient.apiService.getAccounts(getAccountParams)
            if (!responseAccounts.isSuccessful) {
                ApiResult.Failure(responseDGP.errorBody()?.string() ?: "")
            }

            responseAccounts.body()?.result?.let {
                val accounts = when (it.size) {
                    1 -> arrayOf(it[0].toSteemitWallet(dgp))
                    else -> arrayOf()
                }

                ApiResult.Success(accounts)
            } ?: ApiResult.Failure("Failed to read accounts" ?: "")
        }
        catch (e: java.lang.Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }

    override suspend fun readRankedPosts(
        sort: String,
        tag: String,
        observer: String,
        limit: Int,
        existingList: List<PostItem>
    ): ApiResult<List<PostItem>> = withContext(dispatcher) {
        val lastPostAuthor = when {
            (existingList.isEmpty()) -> ""
            else -> existingList.last().account
        }
        val lastPostPermlink = when {
            (existingList.isEmpty()) -> ""
            else -> existingList.last().permlink
        }
        val innerParams = GetRankedPostParamsDTO.InnerParams(
            sort,
            tag,
            observer,
            limit,
            lastPostAuthor,
            lastPostPermlink
        )
        val getRankedPostsParams = GetRankedPostParamsDTO(params = innerParams, id = 1)

        try {
            val response = SteemClient.apiService.getRankedPosts(getRankedPostsParams)
            if (!response.isSuccessful) {
                ApiResult.Failure(response.errorBody()?.string() ?: "")
            }
            else {
                val postItems = (response.body()?.result ?: listOf()).map { postItemDTO ->
                    postItemDTO.toPostItem()
                }

                ApiResult.Success(postItems)
            }
        }
        catch (e: java.lang.Exception) {
            ApiResult.Error(e)
        }
    }

}