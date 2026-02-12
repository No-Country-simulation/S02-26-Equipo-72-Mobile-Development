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

            // Verificar Firebase Auth primero
            val firebaseUser = firebaseAuthService.firebaseAuth.currentUser
            Log.d(TAG, "Firebase currentUser: ${firebaseUser?.uid}")

            if (firebaseUser != null) {
                // Si hay usuario en Firebase, buscar en BD local
                try {
                    val localUser = userDao.getUserById(firebaseUser.uid)
                    if (localUser != null) {
                        Log.d(TAG, "Usuario encontrado: ${localUser.email}")

                        // Sincronizar preferencias si es necesario
                        try {
                            val isLoggedInPref = userPreferences.isLoggedIn.first()
                            if (!isLoggedInPref) {
                                userPreferences.setLoggedIn(true)
                                userPreferences.saveUserId(localUser.id)
                                userPreferences.saveUserEmail(localUser.email)
                            }
                        } catch (prefException: Exception) {
                            Log.w(TAG, "Error sincronizando preferencias: ${prefException.message}")
                        }

                        emit(User(
                            id = localUser.id,
                            email = localUser.email,
                            displayName = localUser.displayName,
                            photoUrl = localUser.photoUrl,
                            createdAt = localUser.createdAt,
                            updatedAt = localUser.updatedAt
                        ))
                    } else {
                        Log.w(TAG, "Usuario en Firebase pero no en BD local")
                        // Crear usuario local desde Firebase
                        val newUser = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName ?: "",
                            photoUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )

                        // Guardar en BD local
                        userDao.insertUser(
                            UserEntity(
                                id = newUser.id,
                                email = newUser.email,
                                displayName = newUser.displayName,
                                photoUrl = newUser.photoUrl,
                                createdAt = newUser.createdAt,
                                updatedAt = newUser.updatedAt
                            )
                        )

                        // Sincronizar preferencias
                        userPreferences.setLoggedIn(true)
                        userPreferences.saveUserId(newUser.id)
                        userPreferences.saveUserEmail(newUser.email)

                        emit(newUser)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error con base de datos local", e)
                    emit(null)
                }
            } else {
                Log.d(TAG, "No hay usuario en Firebase")
                // Limpiar preferencias si están desactualizadas
                try {
                    val isLoggedInPref = userPreferences.isLoggedIn.first()
                    if (isLoggedInPref) {
                        Log.w(TAG, "Limpiando preferencias inconsistentes")
                        userPreferences.clearAll()
                    }
                } catch (prefException: Exception) {
                    Log.w(TAG, "Error limpiando preferencias: ${prefException.message}")
                }
                emit(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en getCurrentUser()", e)
            emit(null)
        }
    }

    override fun isUserAuthenticated(): Flow<Boolean> = flow {
        try {
            // Verificar tanto Firebase como preferencias locales
            val firebaseUser = firebaseAuthService.firebaseAuth.currentUser
            val isLoggedInPref = try {
                userPreferences.isLoggedIn.first()
            } catch (e: Exception) {
                Log.w(TAG, "Error leyendo preferencias: ${e.message}")
                false
            }

            val isAuthenticated = firebaseUser != null && isLoggedInPref
            Log.d(TAG, "isUserAuthenticated: $isAuthenticated (Firebase: ${firebaseUser != null}, Prefs: $isLoggedInPref)")
            emit(isAuthenticated)
        } catch (e: Exception) {
            Log.e(TAG, "Error en isUserAuthenticated", e)
            emit(false)
        }
    }

    /**
     * Método auxiliar para verificar autenticación sin Flow
     */
    suspend fun isUserAuthenticatedSync(): Boolean {
        return try {
            val firebaseUser = firebaseAuthService.firebaseAuth.currentUser
            val isLoggedInPref = try {
                userPreferences.isLoggedIn.first()
            } catch (e: Exception) {
                Log.w(TAG, "Error leyendo preferencias sync: ${e.message}")
                false
            }

            val result = firebaseUser != null && isLoggedInPref
            Log.d(TAG, "isUserAuthenticatedSync: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error en isUserAuthenticatedSync", e)
            false
        }
    }
}
