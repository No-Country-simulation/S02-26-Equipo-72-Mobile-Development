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
 * Tests unitarios para RegisterUseCase
 * Patrón: AAA (Arrange-Act-Assert)
 */
class RegisterUseCaseTest {

    private lateinit var registerUseCase: RegisterUseCase
    private val mockRepository = mockk<IAuthRepository>()

    @Before
    fun setup() {
        registerUseCase = RegisterUseCase(mockRepository)
    }

    /**
     * GIVEN: Email, password y displayName válidos
     * WHEN: Se llama al use case
     * THEN: Retorna AuthResult.Success con nuevo usuario
     */
    @Test
    fun testRegister_WithValidData_ReturnsSuccess() = runTest {
        // Arrange
        val email = "newuser@email.com"
        val password = "SecurePass123"
        val displayName = "Juan García"
        val expectedUser = User(
            id = "newid123",
            email = email,
            displayName = displayName,
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockRepository.signUp(email, password, displayName) } returns flowOf(
            AuthResult.Success(expectedUser)
        )

        // Act
        val result = registerUseCase(email, password, displayName)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Success::class.java)
        val successResult = flowResult as AuthResult.Success
        assertThat(successResult.data.email).isEqualTo(email)
        assertThat(successResult.data.displayName).isEqualTo(displayName)
    }

    /**
     * GIVEN: Email ya registrado
     * WHEN: Se intenta registrar
     * THEN: Retorna AuthResult.Error
     */
    @Test
    fun testRegister_WithExistingEmail_ReturnsError() = runTest {
        // Arrange
        val email = "existing@email.com"
        val password = "password123"
        val displayName = "Test User"
        val errorMessage = "Este email ya está registrado"
        coEvery { mockRepository.signUp(email, password, displayName) } returns flowOf(
            AuthResult.Error(errorMessage)
        )

        // Act
        val result = registerUseCase(email, password, displayName)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Error::class.java)
        val errorResult = flowResult as AuthResult.Error
        assertThat(errorResult.message).contains("registrado")
    }

    /**
     * GIVEN: Contraseña muy corta
     * WHEN: Se intenta registrar
     * THEN: Retorna AuthResult.Error
     */
    @Test
    fun testRegister_WithShortPassword_ReturnsError() = runTest {
        // Arrange
        val email = "user@email.com"
        val password = "123"  // Muy corta
        val displayName = "Test"
        val errorMessage = "La contraseña debe tener al menos 6 caracteres"
        coEvery { mockRepository.signUp(email, password, displayName) } returns flowOf(
            AuthResult.Error(errorMessage)
        )

        // Act
        val result = registerUseCase(email, password, displayName)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Error::class.java)
    }

    /**
     * GIVEN: Email inválido
     * WHEN: Se intenta registrar
     * THEN: Retorna AuthResult.Error
     */
    @Test
    fun testRegister_WithInvalidEmail_ReturnsError() = runTest {
        // Arrange
        val email = "notanemail"  // Sin @
        val password = "password123"
        val displayName = "Test"
        val errorMessage = "El formato del email no es válido"
        coEvery { mockRepository.signUp(email, password, displayName) } returns flowOf(
            AuthResult.Error(errorMessage)
        )

        // Act
        val result = registerUseCase(email, password, displayName)

        // Assert
        val flowResult = result.first()
        assertThat(flowResult).isInstanceOf(AuthResult.Error::class.java)
    }
}
