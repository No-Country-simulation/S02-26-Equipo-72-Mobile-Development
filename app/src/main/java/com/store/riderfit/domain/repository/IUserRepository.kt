package com.store.riderfit.domain.repository

import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun saveUserProfile(user: User): Flow<AuthResult<Unit>>
    fun getUserProfile(userId: String): Flow<AuthResult<User>>
    suspend fun updateUserProfile(user: User): Flow<AuthResult<Unit>>
}
