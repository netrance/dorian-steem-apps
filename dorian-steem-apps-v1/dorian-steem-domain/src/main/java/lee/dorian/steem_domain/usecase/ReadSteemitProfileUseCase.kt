package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

class ReadSteemitProfileUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    operator suspend fun invoke(
        account: String
    ): ApiResult<SteemitProfile> = withContext(dispatcher) {
        steemRepository.readSteemitProfile(account)
    }

}