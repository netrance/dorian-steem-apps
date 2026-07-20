package lee.dorian.steem_data.retrofit

import lee.dorian.steem_data.model.delegation.GetExpiringDelegationsResponseDTO
import lee.dorian.steem_data.model.delegation.GetIncomingDelegationsResponseDTO
import lee.dorian.steem_data.model.delegation.GetOutgoingDelegationsResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SteemWorldService {

    @GET("delegations_api/getIncomingDelegations/{account}")
    suspend fun getIncomingDelegations(
        @Path("account") account: String
    ): Response<GetIncomingDelegationsResponseDTO>

    @GET("delegations_api/getOutgoingDelegations/{account}")
    suspend fun getOutgoingDelegations(
        @Path("account") account: String
    ): Response<GetOutgoingDelegationsResponseDTO>

    @GET("delegations_api/getExpiringDelegations/{account}")
    suspend fun getExpiringDelegations(
        @Path("account") account: String
    ): Response<GetExpiringDelegationsResponseDTO>

}
