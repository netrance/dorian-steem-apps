package lee.dorian.steem_domain.repository

import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import lee.dorian.steem_domain.model.ApiResult
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

}