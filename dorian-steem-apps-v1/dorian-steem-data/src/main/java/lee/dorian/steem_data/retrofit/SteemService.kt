package lee.dorian.steem_data.retrofit

import io.reactivex.Single
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetAccountsResponseDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesResponseDTO
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_data.model.post.GetRankedPostsResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SteemService {

    // Runs condenser_api.get_accounts API.
    @POST(".")
    suspend fun getAccounts(
        @Body followingParams: GetAccountsParamsDTO
    ): Response<GetAccountsResponseDTO>

    @POST(".")
    suspend fun getDynamicGlobalProperties(
        @Body params: GetDynamicGlobalPropertiesParamsDTO
    ): Response<GetDynamicGlobalPropertiesResponseDTO>

    // Runs bridge.get_ranked_posts API.
    @POST(".")
    suspend fun getRankedPosts(
        @Body params: GetRankedPostParamsDTO
    ): Response<GetRankedPostsResponseDTO>

}