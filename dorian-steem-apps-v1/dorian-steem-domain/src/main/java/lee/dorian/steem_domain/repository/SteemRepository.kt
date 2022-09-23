package lee.dorian.steem_domain.repository

import io.reactivex.Single
import lee.dorian.steem_domain.model.SteemitWallet

interface SteemRepository {

    fun readSteemitWallet(account: String): Single<Array<SteemitWallet>>

}