package lee.dorian.steem_domain.usecase

import io.reactivex.Flowable
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class ReadSteemitWalletUseCase(val steemRepository: SteemRepository) {

    operator fun invoke(account: String): Flowable<Array<SteemitWallet>> {
        return steemRepository.readSteemitWallet(account)
    }

}