package lee.dorian.steem_domain.repository

import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.model.SteemitWallet

interface SteemRepository {

    suspend fun readSteemitWallet(
        account: String
    ): ApiResult<Array<SteemitWallet>>

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

}