package lee.dorian.steem_ui.ui.tags

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ui.base.BaseViewModel

class TagsViewModel : BaseViewModel() {

    val limit = GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT

    private val _flowTagsState: MutableStateFlow<TagsState> = MutableStateFlow(TagsState.Loading)
    val flowTagsState = _flowTagsState.asStateFlow()

    var tag = ""
    var sort = GetRankedPostParamsDTO.InnerParams.SORT_TRENDING
    val readRankedPostsUseCase = ReadRankedPostsUseCase(SteemRepositoryImpl())

    fun updateSort(checkedRadioButtonId: Int) = viewModelScope.launch {
        val newSort = when (checkedRadioButtonId) {
            R.id.radiobtn_trending -> GetRankedPostParamsDTO.InnerParams.SORT_TRENDING
            R.id.radiobtn_created -> GetRankedPostParamsDTO.InnerParams.SORT_CREATED
            R.id.radiobtn_payout -> GetRankedPostParamsDTO.InnerParams.SORT_PAYOUT
            else -> GetRankedPostParamsDTO.InnerParams.SORT_TRENDING
        }

        this@TagsViewModel.sort = newSort
    }

    fun readRankedPosts(
        limit: Int = this.limit
    ) = viewModelScope.launch {
        _flowTagsState.emit(TagsState.Loading)
        val apiResult = readRankedPostsUseCase(sort, tag)    //_flowSort.value, _flowTag.value)
        val newTagsState = when (apiResult) {
            is ApiResult.Failure -> TagsState.Failure(apiResult.content)
            is ApiResult.Error -> TagsState.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newTagList = mutableListOf<PostItem>().apply {
                    addAll(apiResult.data)
                }
                TagsState.Success(newTagList)
            }
        }
        _flowTagsState.emit(newTagsState)
    }

    fun appendRankedPosts(
        limit: Int = this.limit
    ) = viewModelScope.launch {
        val recentTagsState = _flowTagsState.value
        val existingRankedPosts = when (recentTagsState) {
            is TagsState.Success -> recentTagsState.tagList
            else -> listOf()
        }

        if (existingRankedPosts.size < GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT) {
            return@launch
        }

        val apiResult = readRankedPostsUseCase(sort, tag, existingList = existingRankedPosts)
        val newTagsState = when (apiResult) {
            is ApiResult.Failure -> TagsState.Failure(apiResult.content)
            is ApiResult.Error -> TagsState.Error(apiResult.throwable)
            is ApiResult.Success -> {
                val newTagList = mutableListOf<PostItem>().apply {
                    addAll(existingRankedPosts)
                    addAll(apiResult.data)
                }
                TagsState.Success(newTagList)
            }
        }
        _flowTagsState.emit(newTagsState)
    }

}