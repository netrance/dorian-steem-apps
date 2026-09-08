package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.model.RewardType
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_domain.usecase.ReadAuthorRewardsUseCase
import lee.dorian.steem_domain.usecase.ReadCurationRewardsUseCase
import lee.dorian.steem_domain.usecase.ReadIncomingTransfersUseCase
import lee.dorian.steem_domain.usecase.ReadOutgoingTransfersUseCase
import lee.dorian.steem_domain.usecase.ReadSteemitWalletUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    val readSteemitWalletUseCase: ReadSteemitWalletUseCase,
    val readOutgoingTransfersUseCase: ReadOutgoingTransfersUseCase,
    val readIncomingTransfersUseCase: ReadIncomingTransfersUseCase,
    val readAuthorRewardsUseCase: ReadAuthorRewardsUseCase,
    val readCurationRewardsUseCase: ReadCurationRewardsUseCase
) : BaseViewModel() {

    private val _flowWalletState = MutableStateFlow<State<SteemitWallet>>(State.Empty)
    val flowWalletState = _flowWalletState.asStateFlow()

    private val _flowSentTransfersState = MutableStateFlow<State<List<Transfer>>>(State.Empty)
    val flowSentTransfersState = _flowSentTransfersState.asStateFlow()

    private val _flowReceivedTransfersState = MutableStateFlow<State<List<Transfer>>>(State.Empty)
    val flowReceivedTransfersState = _flowReceivedTransfersState.asStateFlow()

    private val _flowRewardsState = MutableStateFlow<State<List<Reward>>>(State.Empty)
    val flowRewardsState = _flowRewardsState.asStateFlow()

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

    fun readSentTransfers(account: String) = viewModelScope.launch {
        _flowSentTransfersState.emit(State.Loading)
        val apiResult = readOutgoingTransfersUseCase(account)
        val newSentTransfersState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }

        _flowSentTransfersState.emit(newSentTransfersState)
    }

    fun readReceivedTransfers(account: String) = viewModelScope.launch {
        _flowReceivedTransfersState.emit(State.Loading)
        val apiResult = readIncomingTransfersUseCase(account)
        val newReceivedTransfersState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }

        _flowReceivedTransfersState.emit(newReceivedTransfersState)
    }

    // Both reward types share one state, so selecting a type replaces the list being shown.
    // The time range is epoch seconds, oldest first, and the caller is the one that decides
    // which end of its date range each bound comes from.
    fun readRewards(
        account: String,
        rewardType: RewardType,
        fromTime: Long,
        toTime: Long
    ) = viewModelScope.launch {
        _flowRewardsState.emit(State.Loading)
        val apiResult = when (rewardType) {
            RewardType.AUTHOR -> readAuthorRewardsUseCase(account, fromTime, toTime)
            RewardType.CURATION -> readCurationRewardsUseCase(account, fromTime, toTime)
        }
        val newRewardsState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }

        _flowRewardsState.emit(newRewardsState)
    }

}
