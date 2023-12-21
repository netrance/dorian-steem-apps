package lee.dorian.steem_ui.model

// Existing other state interfaces will be replaced with this one.
sealed interface State<out T> {
    object Empty: State<Nothing>
    object Loading: State<Nothing>
    data class Success<T>(val data: T) : State<T>
    data class Failure(val content: String): State<Nothing>
    data class Error(val throwable: Throwable): State<Nothing>
}