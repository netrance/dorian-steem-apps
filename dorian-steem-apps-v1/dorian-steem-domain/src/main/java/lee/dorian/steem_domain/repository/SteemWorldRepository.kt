package lee.dorian.steem_domain.repository

import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.VestingDelegation

interface SteemWorldRepository {

    suspend fun readIncomingVestingDelegations(
        account: String
    ): ApiResult<List<VestingDelegation>>

    suspend fun readOutgoingVestingDelegations(
        account: String
    ): ApiResult<List<VestingDelegation>>

    suspend fun readExpiringVestingDelegations(
        account: String
    ): ApiResult<List<ExpiringVestingDelegation>>

}
