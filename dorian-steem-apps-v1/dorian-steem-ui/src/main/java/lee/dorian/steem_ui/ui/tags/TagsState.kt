package lee.dorian.steem_ui.ui.tags

import lee.dorian.steem_domain.model.PostItem

sealed interface TagsState {
    object Loading: TagsState
    data class Success(val tagList: List<PostItem>) : TagsState
    data class Failure(val content: String): TagsState
    data class Error(val throwable: Throwable): TagsState
}
