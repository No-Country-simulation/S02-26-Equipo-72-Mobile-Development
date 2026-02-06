package com.store.riderfit.data.repository

import com.store.riderfit.data.local.database.dao.UserDao
import com.store.riderfit.data.local.database.entity.UserEntity
import com.store.riderfit.data.model.toDomain
import com.store.riderfit.data.remote.firebase.FirebaseUserService
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val firebaseUserService: FirebaseUserService,
    private val userDao: UserDao
) : IUserRepository {

    override suspend fun saveUserProfile(user: User): Flow<AuthResult<Unit>> = flow {
        try {
            emitAll(firebaseUserService.saveUserProfile(user).map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Guardar en local
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
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error al guardar perfil"))
        }
    }

    override fun getUserProfile(userId: String): Flow<AuthResult<User>> = flow {
        try {
            emitAll(firebaseUserService.getUserProfile(userId).map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Guardar en local para caché
                        userDao.insertUser(
                            UserEntity(
                                id = result.data.id,
                                email = result.data.email,
                                displayName = result.data.displayName,
                                photoUrl = result.data.photoUrl,
                                createdAt = result.data.createdAt,
                                updatedAt = result.data.updatedAt
                            )
                        )
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error al obtener perfil"))
        }
    }

    override suspend fun updateUserProfile(user: User): Flow<AuthResult<Unit>> = flow {
        try {
            emitAll(firebaseUserService.saveUserProfile(user).map { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Actualizar localmente
                        userDao.updateUser(
                            UserEntity(
                                id = user.id,
                                email = user.email,
                                displayName = user.displayName,
                                photoUrl = user.photoUrl,
                                createdAt = user.createdAt,
                                updatedAt = user.updatedAt
                            )
                        )
                        result
                    }
                    is AuthResult.Error -> result
                    is AuthResult.Loading -> result
                }
            })
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Error al actualizar perfil"))
        }
    }
}
