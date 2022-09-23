package lee.dorian.steem_data.repository

import io.reactivex.Single
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class SteemRepositoryImpl: SteemRepository {

    override fun readSteemitWallet(account: String): Single<Array<SteemitWallet>> {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        return SteemClient.apiService.getAccounts(getAccountParams).map { responseDTO ->
            val result = responseDTO.result ?: arrayOf()
            when (result.size) {
                1 -> arrayOf(result[0].toSteemitWallet())
                else -> arrayOf()
            }
        }
    }

}