package com.store.riderfit.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private const val TAG = "AuthRepositoryImpl"

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
            Log.e(TAG, "Error en signUp", e)
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
            Log.e(TAG, "Error en login", e)
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
            Log.e(TAG, "Error en logout", e)
            emit(AuthResult.Error(e.message ?: "Error desconocido en logout"))
        }
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        try {
            Log.d(TAG, "getCurrentUser() iniciado")

            // Verificar preferencias primero (más rápido)
            val isLoggedIn = userPreferences.isLoggedIn.first()
            Log.d(TAG, "isLoggedIn desde preferencias: $isLoggedIn")

            if (!isLoggedIn) {
                Log.d(TAG, "Usuario no autenticado según preferencias")
                emit(null)
                return@flow
            }

            // Verificar Firebase Auth
            val firebaseUser = firebaseAuthService.firebaseAuth.currentUser
            Log.d(TAG, "Firebase currentUser: ${firebaseUser?.uid}")

            if (firebaseUser != null) {
                // Si hay usuario en Firebase, buscar en BD local
                try {
                    val localUser = userDao.getUserById(firebaseUser.uid)
                    if (localUser != null) {
                        Log.d(TAG, "Usuario encontrado: ${localUser.email}")
                        emit(User(
                            id = localUser.id,
                            email = localUser.email,
                            displayName = localUser.displayName,
                            photoUrl = localUser.photoUrl,
                            createdAt = localUser.createdAt,
                            updatedAt = localUser.updatedAt
                        ))
                    } else {
                        Log.w(TAG, "Usuario en Firebase pero no en BD local, limpiando sesión")
                        // Limpiar preferencias inconsistentes
                        userPreferences.clearAll()
                        emit(null)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error leyendo usuario de BD", e)
                    emit(null)
                }
            } else {
                Log.w(TAG, "Preferencias indican autenticado pero no hay usuario en Firebase, limpiando")
                // Limpiar preferencias inconsistentes
                userPreferences.clearAll()
                emit(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en getCurrentUser()", e)
            emit(null)
        }
    }

    override fun isUserAuthenticated(): Flow<Boolean> =
        userPreferences.isLoggedIn
}
