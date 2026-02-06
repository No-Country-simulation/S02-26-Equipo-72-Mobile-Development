package com.store.riderfit.domain.repository

import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun signUp(email: String, password: String, displayName: String): Flow<AuthResult<User>>
    fun login(email: String, password: String): Flow<AuthResult<User>>
    fun logout(): Flow<AuthResult<Unit>>
    fun getCurrentUser(): Flow<User?>
    fun isUserAuthenticated(): Flow<Boolean>
}
