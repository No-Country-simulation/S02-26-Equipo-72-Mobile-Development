package com.store.riderfit.domain.usecase.auth

import com.store.riderfit.domain.repository.IAuthRepository

class LoginUseCase(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(email: String, password: String) =
        authRepository.login(email, password)
}
