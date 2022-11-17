package lee.dorian.steem_domain.repository

import io.reactivex.Single
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.model.SteemitWallet

interface SteemRepository {

    fun readSteemitWallet(account: String): Single<Array<SteemitWallet>>

    fun readRankedPosts(
        sort: String,
        tag: String,
        observer: String = "",
        limit: Int = 20,
        lastPostItem: PostItem? = null
    ): Single<List<PostItem>>

}