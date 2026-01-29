package lee.dorian.steem_domain.usecase

import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadSteemitWalletUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    operator suspend fun invoke(
        account: String
    ): ApiResult<Array<SteemitWallet>> = withContext(dispatcher) {
        steemRepository.readSteemitWallet(account)
    }

}