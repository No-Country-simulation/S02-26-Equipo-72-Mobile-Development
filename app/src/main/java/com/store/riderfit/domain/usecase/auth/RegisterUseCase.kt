package com.store.riderfit.domain.usecase.auth

import com.store.riderfit.domain.repository.IAuthRepository

class RegisterUseCase(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ) = authRepository.signUp(email, password, displayName)
}
