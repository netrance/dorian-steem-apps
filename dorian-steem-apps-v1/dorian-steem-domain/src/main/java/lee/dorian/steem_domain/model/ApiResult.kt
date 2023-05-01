package lee.dorian.steem_domain.model

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val content: String) : ApiResult<Nothing>()
    data class Error(val throwable: Throwable) : ApiResult<Nothing>()
}