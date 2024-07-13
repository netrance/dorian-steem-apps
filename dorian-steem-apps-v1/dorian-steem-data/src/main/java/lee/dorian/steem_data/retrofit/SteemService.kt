package lee.dorian.steem_data.retrofit

import io.reactivex.Single
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetAccountsResponseDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesResponseDTO
import lee.dorian.steem_data.model.follow.GetFollowCountParamsDTO
import lee.dorian.steem_data.model.follow.GetFollowCountResponseDTO
import lee.dorian.steem_data.model.history.GetAccountHistoryParamsDTO
import lee.dorian.steem_data.model.history.GetAccountHistoryResponseDTO
import lee.dorian.steem_data.model.post.*
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

    // Runs condenser_api.get_follow_count API.
    @POST(".")
    suspend fun getFollowCount(
        @Body followingParams: GetFollowCountParamsDTO
    ): Response<GetFollowCountResponseDTO>

    @POST(".")
    suspend fun getAccountPosts(
        @Body params: GetAccountPostParamsDTO
    ): Response<GetAccountPostsResponseDTO>

    // Runs bridge.get_ranked_posts API.
    @POST(".")
    suspend fun getRankedPosts(
        @Body params: GetRankedPostParamsDTO
    ): Response<GetRankedPostsResponseDTO>

    // Runs bridge.get_discussion API.
    @POST(".")
    suspend fun getDiscussion(
        @Body params: GetDiscussionParamsDTO
    ): Response<GetDiscussionResponseDTO>

    @POST(".")
    suspend fun getAccountHistory(
        @Body params: GetAccountHistoryParamsDTO
    ): Response<GetAccountHistoryResponseDTO>

}