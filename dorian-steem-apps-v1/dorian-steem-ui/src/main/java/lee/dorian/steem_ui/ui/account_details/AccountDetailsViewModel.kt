package lee.dorian.steem_ui.ui.account_details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.usecase.ReadAccountDetailsUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel

class AccountDetailsViewModel(
    private val readAccountDetailsUseCase: ReadAccountDetailsUseCase = ReadAccountDetailsUseCase(SteemRepositoryImpl())
) : BaseViewModel() {

    private val _accountDetailsState: MutableStateFlow<State<AccountDetails>> = MutableStateFlow(State.Empty)
    val accontDetailsState = _accountDetailsState.asStateFlow()

    suspend fun readAccountDetails(account: String) = viewModelScope.launch {
        _accountDetailsState.emit(State.Loading)
        val apiResult = readAccountDetailsUseCase(account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success<AccountDetails>(apiResult.data)
        }
        _accountDetailsState.emit(newState)
    }

}