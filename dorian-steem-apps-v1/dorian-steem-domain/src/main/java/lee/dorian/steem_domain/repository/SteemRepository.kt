package lee.dorian.steem_domain.repository

import io.reactivex.Flowable
import lee.dorian.steem_domain.model.SteemitWalletDTO

interface SteemRepository {

    fun readSteemitWallet(account: String): Flowable<Array<SteemitWalletDTO>>

}