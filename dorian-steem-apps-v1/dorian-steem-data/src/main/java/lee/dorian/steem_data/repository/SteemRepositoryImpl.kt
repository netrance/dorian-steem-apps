package lee.dorian.steem_data.repository

import io.reactivex.Flowable
import lee.dorian.steem_data.model.GetAccountsParams
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_domain.model.SteemitWalletDTO
import lee.dorian.steem_domain.repository.SteemRepository

class SteemRepositoryImpl: SteemRepository {

    override fun readSteemitWallet(account: String): Flowable<Array<SteemitWalletDTO>> {
        val getAccountParams = GetAccountsParams(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        return SteemClient.apiService.getAccounts(getAccountParams).map { responseEntity ->
            when (responseEntity.result.size) {
                1 -> arrayOf(responseEntity.result[0].toSteemitWalletDTO())
                else -> arrayOf()
            }
        }
    }

}