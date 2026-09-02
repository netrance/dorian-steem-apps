package lee.dorian.steem_domain.repository

import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.model.Transfer
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

    // Reads the transfers sent by the given account, latest first.
    suspend fun readOutgoingTransfers(
        account: String,
        offset: Int,
        limit: Int
    ): ApiResult<List<Transfer>>

    // Reads the transfers received by the given account, latest first.
    suspend fun readIncomingTransfers(
        account: String,
        offset: Int,
        limit: Int
    ): ApiResult<List<Transfer>>

    // Reads every author reward paid to the given account in the given time range, latest first.
    suspend fun readAuthorRewards(
        account: String,
        fromTime: Long,
        toTime: Long
    ): ApiResult<List<Reward>>

    // Reads every curation reward paid to the given account in the given time range, latest first.
    suspend fun readCurationRewards(
        account: String,
        fromTime: Long,
        toTime: Long
    ): ApiResult<List<Reward>>

}
