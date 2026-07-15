package lee.dorian.steem_data.retrofit

import lee.dorian.steem_data.model.delegation.GetIncomingDelegationsResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SteemWorldService {

    @GET("delegations_api/getIncomingDelegations/{account}")
    suspend fun getIncomingDelegations(
        @Path("account") account: String
    ): Response<GetIncomingDelegationsResponseDTO>

}
