package lee.dorian.steem_ui.ui.post.content

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val readPostAndRepliesUseCase: ReadPostAndRepliesUseCase
) : BaseViewModel() {

    val author = savedStateHandle.getStateFlow("author", "")
    val permlink = savedStateHandle.getStateFlow("permlink", "")

    private val _flowPostContentState: MutableStateFlow<PostContentState> = MutableStateFlow(PostContentState.Empty)
    val flowPostState = _flowPostContentState.asStateFlow()

    fun initState(author: String, permlink: String) = viewModelScope.launch {
        readPostAndReplies(author, permlink)
    }

    suspend fun readPostAndReplies(author: String, permlink: String) {
        _flowPostContentState.emit(PostContentState.Loading)
        val apiResult = readPostAndRepliesUseCase(author, permlink)
        val newState = when (apiResult) {
            is ApiResult.Failure -> PostContentState.Failure(apiResult.content)
            is ApiResult.Error -> PostContentState.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val postList = mutableListOf<Post>().apply {
                    addAll(apiResult.data)
                }
                val post = postList.removeAt(0)
                val replies = postList
                PostContentState.Success(post, replies)
            }
        }
        _flowPostContentState.emit(newState)
    }

    fun updateAutherAndPermlink(author: String, permlink: String) {
        savedStateHandle["author"] = author
        savedStateHandle["permlink"] = permlink
        viewModelScope.launch {
            readPostAndReplies(author, permlink)
        }
    }

}