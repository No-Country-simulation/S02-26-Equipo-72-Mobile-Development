package com.store.riderfit.data.repository

import com.store.riderfit.data.local.database.dao.UserDao
import com.store.riderfit.data.local.database.entity.UserEntity
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.data.model.toDomain
import com.store.riderfit.data.remote.firebase.FirebaseAuthService
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuthService: FirebaseAuthService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) : IAuthRepository {

    override fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Flow<AuthResult<User>> = flow {
        try {
            emitAll(firebaseAuthService.signUp(email, password).map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Guardar localmente
                        val user = result.data
                        userDao.insertUser(
                            UserEntity(
                                id = user.id,
                                email = user.email,
                                displayName = user.displayName,
                                photoUrl = user.photoUrl,
                                createdAt = user.createdAt,
                                updatedAt = user.updatedAt
                            )
                        )
                        // Guardar sesión
                        userPreferences.saveUserId(user.id)
                        userPreferences.saveUserEmail(user.email)
                        userPreferences.setLoggedIn(true)
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error desconocido en signUp"))
        }
    }

    override fun login(
        email: String,
        password: String
    ): Flow<AuthResult<User>> = flow {
        try {
            emitAll(firebaseAuthService.login(email, password).map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Guardar localmente
                        val user = result.data
                        userDao.insertUser(
                            UserEntity(
                                id = user.id,
                                email = user.email,
                                displayName = user.displayName,
                                photoUrl = user.photoUrl,
                                createdAt = user.createdAt,
                                updatedAt = user.updatedAt
                            )
                        )
                        // Guardar sesión
                        userPreferences.saveUserId(user.id)
                        userPreferences.saveUserEmail(user.email)
                        userPreferences.setLoggedIn(true)
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error desconocido en login"))
        }
    }

    override fun logout(): Flow<AuthResult<Unit>> = flow {
        try {
            emitAll(firebaseAuthService.logout().map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Limpiar localmente
                        userDao.deleteAllUsers()
                        userPreferences.clearAll()
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error desconocido en logout"))
        }
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        try {
            // Primero verificar si hay usuario en local
            emitAll(userPreferences.userId.map { userId ->
                if (userId != null) {
                    val localUser = userDao.getUserById(userId)
                    localUser?.let { entity ->
                        User(
                            id = entity.id,
                            email = entity.email,
                            displayName = entity.displayName,
                            photoUrl = entity.photoUrl,
                            createdAt = entity.createdAt,
                            updatedAt = entity.updatedAt
                        )
                    }
                } else {
                    null
                }
            })
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun isUserAuthenticated(): Flow<Boolean> =
        userPreferences.isLoggedIn
}
