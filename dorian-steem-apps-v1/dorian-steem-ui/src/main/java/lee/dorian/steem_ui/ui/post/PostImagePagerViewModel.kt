package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PostImagePagerViewModel @Inject constructor() : BaseViewModel() {

    private val _flowImageURLs: MutableStateFlow<State<List<String>>> = MutableStateFlow(State.Loading)
    val flowImageURLs = _flowImageURLs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = State.Loading
    )

    fun updateImageURLs(imageURLs: ArrayList<String>) = viewModelScope.launch {
        _flowImageURLs.emit(State.Success(imageURLs))
    }

}
