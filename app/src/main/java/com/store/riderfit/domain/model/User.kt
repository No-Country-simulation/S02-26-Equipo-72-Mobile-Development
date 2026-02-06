package com.store.riderfit.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
