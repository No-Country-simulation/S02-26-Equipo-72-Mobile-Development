package com.store.riderfit.di

import com.store.riderfit.domain.repository.IAuthRepository
import com.store.riderfit.domain.repository.IProductRepository
import com.store.riderfit.domain.repository.IUserRepository
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.auth.LoginUseCase
import com.store.riderfit.domain.usecase.auth.LogoutUseCase
import com.store.riderfit.domain.usecase.auth.RegisterUseCase
import com.store.riderfit.domain.usecase.product.GetProductsUseCase
import com.store.riderfit.domain.usecase.user.GetUserProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Auth Use Cases
    @Singleton
    @Provides
    fun provideLoginUseCase(authRepository: IAuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Singleton
    @Provides
    fun provideRegisterUseCase(authRepository: IAuthRepository): RegisterUseCase =
        RegisterUseCase(authRepository)

    @Singleton
    @Provides
    fun provideLogoutUseCase(authRepository: IAuthRepository): LogoutUseCase =
        LogoutUseCase(authRepository)

    @Singleton
    @Provides
    fun provideGetCurrentUserUseCase(authRepository: IAuthRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(authRepository)

    // User Use Cases
    @Singleton
    @Provides
    fun provideGetUserProfileUseCase(userRepository: IUserRepository): GetUserProfileUseCase =
        GetUserProfileUseCase(userRepository)

    // Product Use Cases
    @Singleton
    @Provides
    fun provideGetProductsUseCase(productRepository: IProductRepository): GetProductsUseCase =
        GetProductsUseCase(productRepository)
}
