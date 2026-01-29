package lee.dorian.steem_ui.ui.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_domain.model.DynamicGlobalProperties
import lee.dorian.steem_domain.usecase.ReadAccountHistoryUseCase
import lee.dorian.steem_domain.usecase.ReadDynamicGlobalPropertiesUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AccountHistoryViewModel @Inject constructor(
    private val readAccountHistoryUseCase: ReadAccountHistoryUseCase,
    private val readDynamicGlobalPropertiesUseCase: ReadDynamicGlobalPropertiesUseCase
) : BaseViewModel() {

    private val _flowAccountHistoryState: MutableStateFlow<State<AccountHistory>> = MutableStateFlow(State.Empty)
    val flowAccountHistoryState = _flowAccountHistoryState.asStateFlow()

    private val _flowDGPState: MutableStateFlow<State<DynamicGlobalProperties>> = MutableStateFlow(State.Empty)
    val flowDGPState = _flowDGPState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.Empty
    )

    private val _flowIsRefreshing = MutableStateFlow(false)
    val flowIsRefreshing = _flowIsRefreshing.asStateFlow()

    fun readDynamicGlobalProperties() = viewModelScope.launch {
        _flowDGPState.emit(State.Loading)
        val apiResult = readDynamicGlobalPropertiesUseCase()
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val dynamicGlobalProperties = apiResult.data
                State.Success(dynamicGlobalProperties)
            }
        }
        _flowDGPState.emit(newState)
    }

    fun readAccountHistory(account: String) = viewModelScope.launch {
        _flowAccountHistoryState.emit(State.Loading)
        val apiResult = readAccountHistoryUseCase(account)
        _flowIsRefreshing.value = false
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val historyItemList = mutableListOf<AccountHistoryItem>().apply {
                    addAll(apiResult.data)
                }
                State.Success(AccountHistory(account, historyItemList))
            }
        }
        _flowAccountHistoryState.emit(newState)
    }

    fun appendAccountHistory() = viewModelScope.launch {
        val recentState: State<AccountHistory> = _flowAccountHistoryState.value
        if (recentState !is State.Success) {
            return@launch
        }

        val existingPosts = recentState.data.historyList
        val account = recentState.data.account

        val apiResult = readAccountHistoryUseCase(account, existingList = existingPosts)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newHistoryItemList = mutableListOf<AccountHistoryItem>().apply {
                    addAll(existingPosts)
                    addAll(apiResult.data)
                }
                State.Success(AccountHistory(account, newHistoryItemList))
            }
        }
        _flowAccountHistoryState.emit(newState)
    }

    fun refreshAccountHistory() {
        if (flowAccountHistoryState.value !is State.Success<AccountHistory>) {
            return
        }

        _flowIsRefreshing.value = true
        val accountHistory = (flowAccountHistoryState.value as State.Success<AccountHistory>).data
        readAccountHistory(accountHistory.account)
    }
}