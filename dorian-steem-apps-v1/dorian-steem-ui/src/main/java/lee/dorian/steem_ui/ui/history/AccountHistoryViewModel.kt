package lee.dorian.steem_ui.ui.history

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.*
import lee.dorian.steem_domain.usecase.ReadAccountHistoryUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel

class AccountHistoryViewModel(
    val readAccountHistoryUseCase: ReadAccountHistoryUseCase = ReadAccountHistoryUseCase(SteemRepositoryImpl())
) : BaseViewModel() {

    private val _flowState: MutableStateFlow<State<AccountHistory>> = MutableStateFlow(State.Empty)
    val flowState = _flowState.asStateFlow()

    fun readAccountHistory(account: String) = viewModelScope.launch {
        _flowState.emit(State.Loading)
        val apiResult = readAccountHistoryUseCase(account)
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
        _flowState.emit(newState)
    }

    fun appendAccountHistory() = viewModelScope.launch {
        val recentState: State<AccountHistory> = _flowState.value
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
        _flowState.emit(newState)
    }

}