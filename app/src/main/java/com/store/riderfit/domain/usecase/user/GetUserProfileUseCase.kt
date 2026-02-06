package com.store.riderfit.domain.usecase.user

import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val userRepository: IUserRepository
) {
    operator fun invoke(userId: String) = userRepository.getUserProfile(userId)
}
