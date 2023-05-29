package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostImagePagerViewModel : ViewModel() {

    private val _flowImageURLs: MutableStateFlow<PostImagePagerState> = MutableStateFlow(PostImagePagerState.Loading)
    val flowImageURLs = _flowImageURLs.asStateFlow()

    fun updateImageURLs(imageURLs: ArrayList<String>) = viewModelScope.launch {
        _flowImageURLs.emit(PostImagePagerState.Success(imageURLs))
    }

}