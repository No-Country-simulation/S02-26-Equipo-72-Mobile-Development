package com.store.riderfit.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.store.riderfit.data.model.UserDto
import com.store.riderfit.data.model.toDomain
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUserService(
    private val firestore: FirebaseFirestore
) {

    fun saveUserProfile(user: User): Flow<AuthResult<Unit>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            
            val userDto = UserDto(
                id = user.id,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
            
            firestore.collection("users")
                .document(user.id)
                .set(userDto)
                .await()
            
            trySend(AuthResult.Success(Unit))
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al guardar perfil"))
        }
        awaitClose()
    }

    fun getUserProfile(userId: String): Flow<AuthResult<User>> = callbackFlow {
        try {
            trySend(AuthResult.Loading())
            
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            if (snapshot.exists()) {
                val userDto = snapshot.toObject(UserDto::class.java)
                if (userDto != null) {
                    trySend(AuthResult.Success(userDto.toDomain()))
                } else {
                    trySend(AuthResult.Error("No se pudo convertir el documento"))
                }
            } else {
                trySend(AuthResult.Error("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Error al obtener perfil"))
        }
        awaitClose()
    }
}
