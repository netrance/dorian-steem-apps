package lee.dorian.steem_data.repository

import io.reactivex.Flowable
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class SteemRepositoryImpl: SteemRepository {

    override fun readSteemitWallet(account: String): Flowable<Array<SteemitWallet>> {
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        return SteemClient.apiService.getAccounts(getAccountParams).map { responseEntity ->
            when (responseEntity.result.size) {
                1 -> arrayOf(responseEntity.result[0].toSteemitWallet())
                else -> arrayOf()
            }
        }
    }

}