package lee.dorian.steem_ui.ui.post.content

import lee.dorian.steem_domain.model.Post

sealed interface PostContentState {
    object Loading: PostContentState
    data class Success(val post: Post, val replies: List<Post>) : PostContentState
    data class Failure(val content: String): PostContentState
    data class Error(val throwable: Throwable): PostContentState
}