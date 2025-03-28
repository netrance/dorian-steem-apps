package lee.dorian.steem_ui.ui.profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.usecase.ReadSteemitProfileUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel

class ProfileViewModel(
    initialState: State<SteemitProfile> = State.Empty,
    private val readSteemitProfileUseCase: ReadSteemitProfileUseCase = ReadSteemitProfileUseCase(SteemRepositoryImpl())
) : BaseViewModel() {

    private val _profileState: MutableStateFlow<State<SteemitProfile>> = MutableStateFlow(initialState)
    val profileState = _profileState.asStateFlow()

    fun readSteemitProfile(account: String) = viewModelScope.launch {
        _profileState.emit(State.Loading)
        val apiResult = readSteemitProfileUseCase(account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success<SteemitProfile>(apiResult.data)
        }
        _profileState.emit(newState)
    }

}