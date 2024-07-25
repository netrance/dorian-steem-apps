package lee.dorian.steem_domain.repository

import lee.dorian.steem_domain.model.*

interface SteemRepository {

    suspend fun readSteemitProfile(
        account: String
    ): ApiResult<SteemitProfile>

    suspend fun readSteemitWallet(
        account: String
    ): ApiResult<Array<SteemitWallet>>

    suspend fun readPosts(
        account: String,
        sort: String,
        observer: String,
        limit: Int,
        existingList: List<PostItem>
    ): ApiResult<List<PostItem>>

    suspend fun readRankedPosts(
        sort: String,
        tag: String,
        observer: String,
        limit: Int,
        //lastPostItem: PostItem? = null
        existingList: List<PostItem>
    ): ApiResult<List<PostItem>>

    suspend fun readPostAndReplies(
        account: String,
        permlink: String
    ): ApiResult<List<Post>>

    suspend fun readAccountHistory(
        account: String,
        limit: Int,
        existingList: List<AccountHistoryItem>
    ): ApiResult<List<AccountHistoryItem>>

}