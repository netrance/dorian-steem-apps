package lee.dorian.steem_domain.usecase

import io.reactivex.Single
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class ReadSteemitWalletUseCase(val steemRepository: SteemRepository) {

    operator fun invoke(account: String): Single<Array<SteemitWallet>> {
        return steemRepository.readSteemitWallet(account)
    }

}