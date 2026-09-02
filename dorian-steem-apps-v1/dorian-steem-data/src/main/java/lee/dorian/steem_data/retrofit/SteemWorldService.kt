package lee.dorian.steem_data.retrofit

import lee.dorian.steem_data.model.delegation.GetExpiringDelegationsResponseDTO
import lee.dorian.steem_data.model.delegation.GetIncomingDelegationsResponseDTO
import lee.dorian.steem_data.model.delegation.GetOutgoingDelegationsResponseDTO
import lee.dorian.steem_data.model.reward.GetRewardsResponseDTO
import lee.dorian.steem_data.model.transfer.GetTransfersResponseDTO
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

    // Returns the transfers of the given type sent by the given account, latest first.
    @GET("transfers_api/getTransfersByTypeFrom/{type}/{from}/{orderBy}/{orderDir}/{limit}/{offset}")
    suspend fun getTransfersByTypeFrom(
        @Path("from") from: String,
        @Path("offset") offset: Int = DEFAULT_OFFSET,
        @Path("limit") limit: Int = DEFAULT_TRANSFERS_LIMIT,
        @Path("type") type: String = TRANSFER_TYPE_TRANSFER,
        @Path("orderBy") orderBy: String = ORDER_BY_TIME,
        @Path("orderDir") orderDir: String = ORDER_DIR_DESC
    ): Response<GetTransfersResponseDTO>

    // Returns the transfers of the given type received by the given account, latest first.
    @GET("transfers_api/getTransfersByTypeTo/{type}/{to}/{orderBy}/{orderDir}/{limit}/{offset}")
    suspend fun getTransfersByTypeTo(
        @Path("to") to: String,
        @Path("offset") offset: Int = DEFAULT_OFFSET,
        @Path("limit") limit: Int = DEFAULT_TRANSFERS_LIMIT,
        @Path("type") type: String = TRANSFER_TYPE_TRANSFER,
        @Path("orderBy") orderBy: String = ORDER_BY_TIME,
        @Path("orderDir") orderDir: String = ORDER_DIR_DESC
    ): Response<GetTransfersResponseDTO>

    // Returns the rewards of the given op paid to the given account in the given time range,
    // oldest first. rewards_api has no ordering parameter, and it cuts the rows that exceed
    // the limit off the newest end of the time range.
    @GET("rewards_api/getRewards/{op}/{account}/{fromTime}-{toTime}/{limit}/{offset}")
    suspend fun getRewards(
        @Path("op") op: String,
        @Path("account") account: String,
        @Path("fromTime") fromTime: Long = MIN_REWARD_TIME,
        @Path("toTime") toTime: Long = MAX_REWARD_TIME,
        @Path("offset") offset: Int = DEFAULT_OFFSET,
        @Path("limit") limit: Int = DEFAULT_REWARDS_LIMIT
    ): Response<GetRewardsResponseDTO>

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_TRANSFERS_LIMIT = 250
        const val MAX_TRANSFERS_LIMIT = 1000

        // The op types supported by transfers_api.
        const val TRANSFER_TYPE_TRANSFER = "transfer"
        const val TRANSFER_TYPE_TRANSFER_TO_VESTING = "transfer_to_vesting"
        const val TRANSFER_TYPE_WITHDRAW_VESTING = "withdraw_vesting"
        const val TRANSFER_TYPE_TRANSFER_TO_SAVINGS = "transfer_to_savings"
        const val TRANSFER_TYPE_TRANSFER_FROM_SAVINGS = "transfer_from_savings"
        const val TRANSFER_TYPE_CANCEL_TRANSFER_FROM_SAVINGS = "cancel_transfer_from_savings"

        const val ORDER_BY_TIME = "time"
        const val ORDER_DIR_DESC = "DESC"

        const val DEFAULT_REWARDS_LIMIT = 250
        const val MAX_REWARDS_LIMIT = 10000

        // rewards_api rejects 0 as fromTime, so the whole history is read as MIN - MAX.
        const val MIN_REWARD_TIME = 1L
        const val MAX_REWARD_TIME = 9999999999L

        // The op types supported by rewards_api.
        const val REWARD_OP_AUTHOR = "author_reward"
        const val REWARD_OP_CURATION = "curation_reward"
        const val REWARD_OP_COMMENT_BENEFACTOR = "comment_benefactor_reward"
        const val REWARD_OP_PRODUCER = "producer_reward"
    }

}
