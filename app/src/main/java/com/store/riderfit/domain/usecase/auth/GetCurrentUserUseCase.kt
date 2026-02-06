package com.store.riderfit.domain.usecase.auth

import com.store.riderfit.domain.repository.IAuthRepository

class GetCurrentUserUseCase(
    private val authRepository: IAuthRepository
) {
    operator fun invoke() = authRepository.getCurrentUser()
}
