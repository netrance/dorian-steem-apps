package lee.dorian.steem_ui.ui.post

sealed interface PostImagePagerState {
    object Loading : PostImagePagerState
    data class Success(val imageURLList: ArrayList<String>) : PostImagePagerState
    data class Failure(val content: String): PostImagePagerState
    data class Error(val throwable: Throwable): PostImagePagerState
}