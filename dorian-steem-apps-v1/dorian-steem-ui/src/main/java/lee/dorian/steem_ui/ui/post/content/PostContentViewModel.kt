package lee.dorian.steem_ui.ui.post.content

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.ContentHistory
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.usecase.ReadContentHistoryUseCase
import lee.dorian.steem_domain.usecase.ReadPostAndRepliesUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.model.navigation.PostContentRoute
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val readPostAndRepliesUseCase: ReadPostAndRepliesUseCase,
    private val readContentHistoryUseCase: ReadContentHistoryUseCase
) : BaseViewModel() {

    val postContentRoute: PostContentRoute = savedStateHandle.toRoute()

    private val _flowPostContentState: MutableStateFlow<PostContentState> = MutableStateFlow(PostContentState.Empty)
    val flowPostState = _flowPostContentState.asStateFlow()

    // The revisions of this post, latest first. It is kept apart from flowPostState because the
    // post is read on entering the screen while the revisions are only worth reading once they
    // are asked for, and because a failure to read them must not take the post down with it.
    private val _flowContentHistoryState = MutableStateFlow<State<List<ContentHistory>>>(State.Empty)
    val flowContentHistoryState = _flowContentHistoryState.asStateFlow()

    fun readPostAndReplies() = viewModelScope.launch {
        _flowPostContentState.emit(PostContentState.Loading)
        val apiResult = readPostAndRepliesUseCase(postContentRoute.author, postContentRoute.permlink)
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

    // Reads every revision of this post, latest first. A post that has never been edited has
    // exactly one revision, so an empty list means the post could not be read rather than that
    // it was never edited.
    fun readContentHistory() = viewModelScope.launch {
        _flowContentHistoryState.emit(State.Loading)
        val apiResult = readContentHistoryUseCase(postContentRoute.author, postContentRoute.permlink)
        val newContentHistoryState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> State.Success(apiResult.data)
        }

        _flowContentHistoryState.emit(newContentHistoryState)
    }

    fun updateAutherAndPermlink(author: String, permlink: String) {
        savedStateHandle["author"] = author
        savedStateHandle["permlink"] = permlink
        viewModelScope.launch {
            readPostAndReplies()
        }
    }

}