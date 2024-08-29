package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.usecase.ReadSteemitWalletUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel

class WalletViewModel(
    val readSteemitWalletUseCase: ReadSteemitWalletUseCase = ReadSteemitWalletUseCase(SteemRepositoryImpl())
) : BaseViewModel() {

    val _flowWalletState = MutableStateFlow<State<SteemitWallet>>(State.Empty)
    val flowWalletState = _flowWalletState.asStateFlow()

    fun readSteemitWallet(account: String) = viewModelScope.launch {
        _flowWalletState.emit(State.Loading)
        val apiResult = readSteemitWalletUseCase(account)
        val newWalletState = when (apiResult) {
            is ApiResult.Failure -> {
                State.Failure(apiResult.content)
            }
            is ApiResult.Error -> {
                State.Error(apiResult.throwable)
            }
            is ApiResult.Success -> {
                val steemitWalletList = apiResult.data
                State.Success(when {
                    apiResult.data.isNotEmpty() -> steemitWalletList[0]
                    else -> SteemitWallet()
                })
            }
        }

        _flowWalletState.emit(newWalletState)
    }

}