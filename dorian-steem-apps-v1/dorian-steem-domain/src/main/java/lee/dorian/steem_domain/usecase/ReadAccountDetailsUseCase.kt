package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.repository.SteemRepository

class ReadAccountDetailsUseCase(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator suspend fun invoke(
        account: String
    ): ApiResult<AccountDetails> = withContext(dispatcher) {
        steemRepository.readAccountDetails(account)
    }

}