package lee.dorian.steem_data.repository

import io.reactivex.Single
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

    override fun readSteemitWallet(account: String): Single<Array<SteemitWallet>> {
        val getDynamicGlobalPropertiesParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        return Single.zip(
            SteemClient.apiService.getDynamicGlobalProperties(getDynamicGlobalPropertiesParams),
            SteemClient.apiService.getAccounts(getAccountParams)
        ) { getDynamicGlobalPropertiesResponse, getAccountsResponseDTO ->
            val getAccountsResponseResult = getAccountsResponseDTO.result ?: arrayOf()
            when (getAccountsResponseResult.size) {
                1 -> arrayOf(
                    getAccountsResponseResult[0].toSteemitWallet(
                        getDynamicGlobalPropertiesResponse.result
                    )
                )
                else -> arrayOf()
            }
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

        // return
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