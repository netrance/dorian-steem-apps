package lee.dorian.steem_data.retrofit

import io.reactivex.Single
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetAccountsResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface SteemService {

    // Runs condenser_api.get_accounts API.
    @POST(".")
    fun getAccounts(
        @Body followingParams: GetAccountsParamsDTO
    ): Single<GetAccountsResponseDTO>

}