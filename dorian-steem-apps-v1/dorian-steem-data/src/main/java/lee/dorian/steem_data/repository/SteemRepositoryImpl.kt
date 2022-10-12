package lee.dorian.steem_data.repository

import io.reactivex.Single
import lee.dorian.steem_data.model.GetAccountsParamsDTO
import lee.dorian.steem_data.model.GetDynamicGlobalPropertiesParamsDTO
import lee.dorian.steem_data.retrofit.SteemClient
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.repository.SteemRepository

class SteemRepositoryImpl: SteemRepository {

    override fun readSteemitWallet(account: String): Single<Array<SteemitWallet>> {
        val getDynamicGlobalPropertiesParams = GetDynamicGlobalPropertiesParamsDTO(id = 1)
        val getAccountParams = GetAccountsParamsDTO(
            params = arrayOf(arrayOf(account)),
            id = 1
        )

        return Single.zip(
            SteemClient.apiService.getDynamicGlobalProperties(getDynamicGlobalPropertiesParams),
            SteemClient.apiService.getAccounts(getAccountParams)
        ) { getDynamicGlobalPropertiesResponse, getAccountsResponseDTO ->
            val getAccountsResponseResult = getAccountsResponseDTO.result ?: arrayOf()
            when (getAccountsResponseResult.size) {
                1 -> arrayOf(
                    getAccountsResponseResult[0].toSteemitWallet(
                        getDynamicGlobalPropertiesResponse.result
                    )
                )
                else -> arrayOf()
            }
        }
    }

}