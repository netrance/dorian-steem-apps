package lee.dorian.steem_domain.usecase

import io.reactivex.Flowable
import lee.dorian.steem_domain.model.SteemitWalletDTO
import lee.dorian.steem_domain.repository.SteemRepository

class ReadSteemitWalletUseCase(val steemRepository: SteemRepository) {

    operator fun invoke(account: String): Flowable<Array<SteemitWalletDTO>> {
        return steemRepository.readSteemitWallet(account)
    }

}