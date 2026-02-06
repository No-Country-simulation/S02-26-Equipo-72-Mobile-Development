package com.store.riderfit.domain.usecase.auth

import com.store.riderfit.domain.repository.IAuthRepository

class LogoutUseCase(
    private val authRepository: IAuthRepository
) {
    operator fun invoke() = authRepository.logout()
}
