package com.store.riderfit.data.model

import com.store.riderfit.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

// Mapeos
fun UserDto.toDomain() = User(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun User.toDto() = UserDto(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)
