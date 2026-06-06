package lee.dorian.steem_ui.ui.tags

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val readRankedPostsUseCase: ReadRankedPostsUseCase
) : BaseViewModel() {

    val limit = DEFAULT_RANKED_POSTS_LIMIT

    private val _flowTagsState: MutableStateFlow<State<List<PostItem>>> = MutableStateFlow(State.Empty)
    val flowTagsState = _flowTagsState.asStateFlow()

    fun isContentEmpty(): Boolean {
        return if (_flowTagsState.value !is State.Success) {
            true
        }
        else {
            (_flowTagsState.value as State.Success).data.isEmpty()
        }
    }

    fun readRankedPosts(
        tag: String,
        sort: String,
        limit: Int = this.limit
    ) = viewModelScope.launch {
        _flowTagsState.emit(State.Loading)
        val apiResult = readRankedPostsUseCase(sort, tag)
        val newTagsState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newTagList = mutableListOf<PostItem>().apply {
                    addAll(apiResult.data)
                }
                State.Success(newTagList)
            }
        }
        _flowTagsState.emit(newTagsState)
    }

    fun appendRankedPosts(
        tag: String,
        sort: String,
        limit: Int = this.limit
    ) = viewModelScope.launch {
        val recentTagsState = _flowTagsState.value
        val existingRankedPosts = when (recentTagsState) {
            is State.Success -> recentTagsState.data
            else -> listOf()
        }

        if (existingRankedPosts.size < DEFAULT_RANKED_POSTS_LIMIT) {
            return@launch
        }

        val apiResult = readRankedPostsUseCase(sort, tag, existingList = existingRankedPosts)
        val newTagsState = when (apiResult) {
            is ApiResult.Failure -> State.Failure(apiResult.content)
            is ApiResult.Error -> State.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newTagList = mutableListOf<PostItem>().apply {
                    addAll(existingRankedPosts)
                    addAll(apiResult.data)
                }
                State.Success(newTagList)
            }
        }
        _flowTagsState.emit(newTagsState)
    }

    companion object {
        const val DEFAULT_RANKED_POSTS_LIMIT = 20
    }

}
