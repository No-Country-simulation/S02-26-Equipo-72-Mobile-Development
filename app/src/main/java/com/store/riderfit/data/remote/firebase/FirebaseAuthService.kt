package com.store.riderfit.data.remote.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.store.riderfit.data.model.AuthState
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebaseAuthService"

class FirebaseAuthService(
    val firebaseAuth: FirebaseAuth
) {

    init {
        Log.d(TAG, "FirebaseAuthService inicializado")
        Log.d(TAG, "FirebaseAuth instance: $firebaseAuth")
        val currentUser = firebaseAuth.currentUser
        Log.d(TAG, "Usuario actual en Firebase: $currentUser")
    }

    fun signUp(email: String, password: String): Flow<AuthResult<User>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val domainUser = User(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                trySend(AuthResult.Success(domainUser))
            } else {
                trySend(AuthResult.Error("Usuario no creado"))
            }
            close()
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("email address is already in use", ignoreCase = true) == true -> 
                    "Este email ya está registrado. Intenta con otro o inicia sesión."
                e.message?.contains("password should be at least 6 characters", ignoreCase = true) == true ->
                    "La contraseña debe tener al menos 6 caracteres."
                e.message?.contains("malformed email address", ignoreCase = true) == true ->
                    "El formato del email no es válido."
                else -> e.message ?: "Error al registrarse"
            }
            trySend(AuthResult.Error(errorMessage))
            close()
        }
    }

    fun login(email: String, password: String): Flow<AuthResult<User>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val domainUser = User(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                trySend(AuthResult.Success(domainUser))
            } else {
                trySend(AuthResult.Error("Login fallido"))
            }
            close()
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "No existe cuenta con este email. Regístrate para crear una."
                e.message?.contains("wrong-password", ignoreCase = true) == true ->
                    "Contraseña incorrecta. Intenta nuevamente."
                e.message?.contains("invalid-email", ignoreCase = true) == true ->
                    "El formato del email no es válido."
                e.message?.contains("user disabled", ignoreCase = true) == true ->
                    "Esta cuenta ha sido deshabilitada."
                else -> e.message ?: "Error al iniciar sesión"
            }
            trySend(AuthResult.Error(errorMessage))
            close()
        }
    }

    fun logout(): Flow<AuthResult<Unit>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            firebaseAuth.signOut()
            trySend(AuthResult.Success(Unit))
            close()
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al cerrar sesión"))
            close()
        }
    }

    fun getCurrentUser(): Flow<User?> = callbackFlow {
        try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val domainUser = User(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                trySend(domainUser)
            } else {
                trySend(null)
            }
        } catch (e: Exception) {
            trySend(null)
        }
        close()
    }

}
