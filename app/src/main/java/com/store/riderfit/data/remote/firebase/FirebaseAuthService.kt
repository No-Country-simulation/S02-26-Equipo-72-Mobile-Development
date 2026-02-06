package com.store.riderfit.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.store.riderfit.data.model.AuthState
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth
) {

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
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al registrarse"))
        }
        awaitClose()
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
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al iniciar sesión"))
        }
        awaitClose()
    }

    fun logout(): Flow<AuthResult<Unit>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            firebaseAuth.signOut()
            trySend(AuthResult.Success(Unit))
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al cerrar sesión"))
        }
        awaitClose()
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
        awaitClose()
    }
}
