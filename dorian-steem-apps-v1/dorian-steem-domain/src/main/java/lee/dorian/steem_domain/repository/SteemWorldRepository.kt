package lee.dorian.steem_domain.repository

import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.VestingDelegation

interface SteemWorldRepository {

    suspend fun readIncomingVestingDelegations(
        account: String
    ): ApiResult<List<VestingDelegation>>

}
