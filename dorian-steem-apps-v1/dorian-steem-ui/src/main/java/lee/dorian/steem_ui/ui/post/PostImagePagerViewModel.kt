package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lee.dorian.steem_ui.model.State

class PostImagePagerViewModel : ViewModel() {

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