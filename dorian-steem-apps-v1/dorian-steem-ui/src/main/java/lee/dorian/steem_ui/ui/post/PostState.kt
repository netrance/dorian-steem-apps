package lee.dorian.steem_ui.ui.post

import lee.dorian.steem_domain.model.Post

sealed interface PostState {
    object Loading: PostState
    data class Success(val post: Post, val replies: List<Post>) : PostState
    data class Failure(val content: String): PostState
    data class Error(val throwable: Throwable): PostState
}