package lee.dorian.steem_ui.ui.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.usecase.ReadSteemitProfileUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val readSteemitProfileUseCase: ReadSteemitProfileUseCase
) : BaseViewModel() {

    private val _profileState: MutableStateFlow<State<SteemitProfile>> = MutableStateFlow(State.Empty)
    val profileState = _profileState.asStateFlow()

    fun getCurrentAccount(): String {
        return if (profileState.value is State.Success<SteemitProfile>) {
            (profileState.value as State.Success<SteemitProfile>).data.account
        } else {
            ""
        }
    }

    fun readSteemitProfile(account: String) = viewModelScope.launch {
        _profileState.emit(State.Loading)
        val apiResult = readSteemitProfileUseCase(account)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }
        _profileState.emit(newState)
    }

    fun readSteemitProfile(stemitProfile: SteemitProfile) {
        _profileState.value = State.Success(stemitProfile)
    }

}