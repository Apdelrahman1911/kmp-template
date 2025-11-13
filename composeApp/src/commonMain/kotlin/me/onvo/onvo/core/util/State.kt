package me.onvo.onvo.core.util


sealed class State<out T> {
    data class Success<T>(val data: T) : State<T>()
    data class Error(val exception: Throwable, val message: String? = null) : State<Nothing>()
    data object Loading : State<Nothing>()
}

inline fun <T> State<T>.onSuccess(action: (T) -> Unit): State<T> {
    if (this is State.Success) action(data)
    return this
}

inline fun <T> State<T>.onError(action: (Throwable) -> Unit): State<T> {
    if (this is State.Error) action(exception)
    return this
}