package lee.dorian.steem_ui.ui.post.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lee.dorian.steem_data.model.post.GetAccountPostParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.usecase.ReadPostsUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel

class PostListViewModel(
    private val readPostsUseCase: ReadPostsUseCase = ReadPostsUseCase(SteemRepositoryImpl())
) : BaseViewModel() {

    val limit = GetAccountPostParamsDTO.InnerParams.DEFAULT_LIMIT

    private val _flowState: MutableStateFlow<State<PostListInfo>> = MutableStateFlow(State.Empty)
    val flowState = _flowState.asStateFlow()

    private val _flowIsRefreshing = MutableStateFlow(false)
    val flowIsRefreshing = _flowIsRefreshing.asStateFlow()

    fun readPosts(author: String, sort: String) = viewModelScope.launch {
        _flowState.emit(State.Loading)
        val apiResult = readPostsUseCase(author, sort, "")
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val postList = mutableListOf<PostItem>().apply {
                    addAll(apiResult.data)
                }
                State.Success(PostListInfo(author, sort, postList))
            }
        }
        _flowState.emit(newState)
    }

    fun appendPosts() = viewModelScope.launch {
        val recentState: State<PostListInfo> = _flowState.value
        if (recentState !is State.Success) {
            return@launch
        }

        val author = recentState.data.author
        val sort = recentState.data.sort
        val existingPosts = when (recentState) {
            is State.Success -> recentState.data.posts
            else -> mutableListOf()
        }

        val apiResult = readPostsUseCase(author, sort, existingList = existingPosts)
        val newState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newPostList = mutableListOf<PostItem>().apply {
                    addAll(existingPosts)
                    addAll(apiResult.data)
                }
                State.Success(PostListInfo(author, sort, newPostList))
            }
        }
        _flowState.emit(newState)
    }

}