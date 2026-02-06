package com.store.riderfit.presentation.state

/**
 * Estado genérico para UI
 * Representa los posibles estados de una operación asíncrona
 */
sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
    class Loading<T> : UiState<T>()
    class Idle<T> : UiState<T>()
}

/**
 * Extensión para mapear estados
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> UiState.Error(message)
    is UiState.Loading -> UiState.Loading()
    is UiState.Idle -> UiState.Idle()
}

/**
 * Extensión para manejar estados
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) action(message)
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) action()
    return this
}
