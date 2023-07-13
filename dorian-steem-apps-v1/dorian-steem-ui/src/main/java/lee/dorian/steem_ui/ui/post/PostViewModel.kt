package lee.dorian.steem_ui.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase

class PostViewModel(
    private val readPostAndRepliesUseCase: ReadPostAndRepliesUseCase = ReadPostAndRepliesUseCase(SteemRepositoryImpl())
) : ViewModel() {

    private val _flowPostState: MutableStateFlow<PostState> = MutableStateFlow(PostState.Loading)
    val flowPostState = _flowPostState.asStateFlow()

    fun readPostAndReplies(author: String, permlink: String) = viewModelScope.launch {
        _flowPostState.emit(PostState.Loading)
        val apiResult = readPostAndRepliesUseCase(author, permlink)
        val newState = when (apiResult) {
            is ApiResult.Failure -> PostState.Failure(apiResult.content)
            is ApiResult.Error -> PostState.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val postList = mutableListOf<Post>().apply {
                    addAll(apiResult.data)
                }
                val post = postList.removeAt(0)
                val replies = postList
                PostState.Success(post, replies)
            }
        }
        _flowPostState.emit(newState)
    }

}