package com.store.riderfit.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para LoginUseCase
 * Patrón: AAA (Arrange-Act-Assert)
 * Principios FIRST: Fast, Independent, Repeatable, Self-validating, Timely
 */
class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private val mockRepository = mockk<IAuthRepository>()

    @Before
    fun setup() {
        loginUseCase = LoginUseCase(mockRepository)
    }

    /**
     * GIVEN: Email y password válidos
     * WHEN: Se llama al use case
     * THEN: Retorna AuthResult.Success con usuario
     */
    @Test
    fun testLogin_WithValidCredentials_ReturnsSuccess() = runTest {
        // Arrange
        val email = "user@email.com"
        val password = "password123"
        val expectedUser = User(
            id = "123",
            email = email,
            displayName = "Test User",
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockRepository.login(email, password) } returns flowOf(
            AuthResult.Success(expectedUser)
        )

        // Act
        val result = loginUseCase(email, password)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Success::class.java)
        val successResult = flowResult as AuthResult.Success<User>
        assertThat(successResult.data.email).isEqualTo(email)
        assertThat(successResult.data.displayName).isEqualTo("Test User")
    }

    /**
     * GIVEN: Email no registrado
     * WHEN: Se intenta hacer login
     * THEN: Retorna AuthResult.Error con mensaje
     */
    @Test
    fun testLogin_WithUnregisteredEmail_ReturnsError() = runTest {
        // Arrange
        val email = "notfound@email.com"
        val password = "password123"
        val errorMessage = "Usuario no encontrado"
        coEvery { mockRepository.login(email, password) } returns flowOf(
            AuthResult.Error(errorMessage)
        )

        // Act
        val result = loginUseCase(email, password)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Error::class.java)
        val errorResult = flowResult as AuthResult.Error<User>
        assertThat(errorResult.message).isEqualTo(errorMessage)
    }

    /**
     * GIVEN: Contraseña incorrecta
     * WHEN: Se intenta hacer login
     * THEN: Retorna AuthResult.Error
     */
    @Test
    fun testLogin_WithWrongPassword_ReturnsError() = runTest {
        // Arrange
        val email = "user@email.com"
        val password = "wrongpassword"
        val errorMessage = "Contraseña incorrecta"
        coEvery { mockRepository.login(email, password) } returns flowOf(
            AuthResult.Error(errorMessage)
        )

        // Act
        val result = loginUseCase(email, password)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Error::class.java)
    }

    /**
     * GIVEN: Loading state antes del resultado
     * WHEN: Se ejecuta el login
     * THEN: Emite Loading seguido de Success
     */
    @Test
    fun testLogin_EmitsLoadingBeforeSuccess() = runTest {
        // Arrange
        val email = "user@email.com"
        val password = "password123"
        val expectedUser = User(
            id = "123",
            email = email,
            displayName = "Test User",
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockRepository.login(email, password) } returns flowOf(
            AuthResult.Loading<User>(),
            AuthResult.Success(expectedUser)
        )

        // Act
        val result = loginUseCase(email, password)

        // Assert
        val results = result.toList()
        assertThat(results).hasSize(2)
        assertThat(results[0]).isInstanceOf(AuthResult.Loading::class.java)
        assertThat(results[1]).isInstanceOf(AuthResult.Success::class.java)
    }
}
