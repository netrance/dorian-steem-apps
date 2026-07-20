package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_domain.usecase.ReadExpiringVestingDelegationsUseCase
import lee.dorian.steem_domain.usecase.ReadOutgoingVestingDelegationsUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.model.navigation.OutgoingDelegationListRoute
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class OutgoingDelegationListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readOutgoingVestingDelegationsUseCase: ReadOutgoingVestingDelegationsUseCase,
    private val readExpiringVestingDelegationsUseCase: ReadExpiringVestingDelegationsUseCase
) : BaseViewModel() {

    val route: OutgoingDelegationListRoute = savedStateHandle.toRoute()

    private val _flowOutgoingDelegationList = MutableStateFlow<State<List<VestingDelegation>>>(State.Empty)
    val flowOutgoingDelegatingList = _flowOutgoingDelegationList.asStateFlow()

    private val _flowExpiringDelegationList = MutableStateFlow<State<List<ExpiringVestingDelegation>>>(State.Empty)
    val flowExpiringDelegationList = _flowExpiringDelegationList.asStateFlow()

    init {
        readDelegatingList()
        readExpiringDelegationList()
    }

    fun readDelegatingList() = viewModelScope.launch {
        _flowOutgoingDelegationList.emit(State.Loading)
        val apiResult = readOutgoingVestingDelegationsUseCase(route.account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }
        _flowOutgoingDelegationList.emit(newState)
    }

    fun readExpiringDelegationList() = viewModelScope.launch {
        _flowExpiringDelegationList.emit(State.Loading)
        val apiResult = readExpiringVestingDelegationsUseCase(route.account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }
        _flowExpiringDelegationList.emit(newState)
    }

}
