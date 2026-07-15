package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.usecase.ReadIncomingVestingDelegationsUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.model.navigation.IncomingDelegationListRoute
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class IncomingDelegationListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readIncomingVestingDelegationsUseCase: ReadIncomingVestingDelegationsUseCase
) : BaseViewModel() {

    val route: IncomingDelegationListRoute = savedStateHandle.toRoute()

    private val _flowIncomingDelegationList = MutableStateFlow<State<List<VestingDelegation>>>(State.Empty)
    val flowIncomingDelegationList = _flowIncomingDelegationList.asStateFlow()

    init {
        readIncomingDelegationList()
    }

    fun readIncomingDelegationList() = viewModelScope.launch {
        _flowIncomingDelegationList.emit(State.Loading)
        val apiResult = readIncomingVestingDelegationsUseCase(route.account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }
        _flowIncomingDelegationList.emit(newState)
    }

}
