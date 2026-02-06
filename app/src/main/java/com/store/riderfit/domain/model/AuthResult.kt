package com.store.riderfit.domain.model

sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val message: String) : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
}
