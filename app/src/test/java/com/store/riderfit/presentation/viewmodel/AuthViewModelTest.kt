package com.store.riderfit.presentation.viewmodel

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.auth.LoginUseCase
import com.store.riderfit.domain.usecase.auth.LogoutUseCase
import com.store.riderfit.domain.usecase.auth.RegisterUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para AuthViewModel
 * Patrón: AAA (Arrange-Act-Assert)
 * Validamos: State management, validaciones, flujo de login/register/logout
 */
class AuthViewModelTest {

    private lateinit var authViewModel: AuthViewModel
    private val mockLoginUseCase = mockk<LoginUseCase>()
    private val mockRegisterUseCase = mockk<RegisterUseCase>()
    private val mockLogoutUseCase = mockk<LogoutUseCase>()
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set Main dispatcher for viewModelScope
        Dispatchers.setMain(testDispatcher)
        
        // Mock android.util.Log to prevent "Method d in android.util.Log not mocked" error
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)
        authViewModel = AuthViewModel(
            loginUseCase = mockLoginUseCase,
            registerUseCase = mockRegisterUseCase,
            logoutUseCase = mockLogoutUseCase,
            getCurrentUserUseCase = mockGetCurrentUserUseCase
        )
    }

    @After
    fun tearDown() {
        // Reset Main dispatcher
        Dispatchers.resetMain()
    }

    /**
     * GIVEN: Email válido ingresado en el formulario
     * WHEN: Se actualiza el email
     * THEN: El estado se actualiza sin errores
     */
    @Test
    fun testOnEmailChanged_WithValidEmail_UpdatesStateWithoutError() = runTest {
        // Arrange
        val validEmail = "user@email.com"

        // Act
        authViewModel.onEmailChanged(validEmail)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.email).isEqualTo(validEmail)
        assertThat(currentState.isEmailValid).isTrue()
        assertThat(currentState.emailError).isNull()
    }

    /**
     * GIVEN: Email inválido ingresado
     * WHEN: Se valida el email
     * THEN: Se muestra mensaje de error
     */
    @Test
    fun testOnEmailChanged_WithInvalidEmail_ShowsError() = runTest {
        // Arrange
        val invalidEmail = "notanemail"

        // Act
        authViewModel.onEmailChanged(invalidEmail)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.isEmailValid).isFalse()
        assertThat(currentState.emailError).isNotEmpty()
    }

    /**
     * GIVEN: Contraseña válida (6+ caracteres)
     * WHEN: Se ingresa
     * THEN: Es válida sin error
     */
    @Test
    fun testOnPasswordChanged_WithValidPassword_IsValid() = runTest {
        // Arrange
        val validPassword = "password123"

        // Act
        authViewModel.onPasswordChanged(validPassword)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.password).isEqualTo(validPassword)
        assertThat(currentState.isPasswordValid).isTrue()
        assertThat(currentState.passwordError).isNull()
    }

    /**
     * GIVEN: Contraseña muy corta (< 6 caracteres)
     * WHEN: Se valida
     * THEN: Muestra error
     */
    @Test
    fun testOnPasswordChanged_WithShortPassword_ShowsError() = runTest {
        // Arrange
        val shortPassword = "123"

        // Act
        authViewModel.onPasswordChanged(shortPassword)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.isPasswordValid).isFalse()
        assertThat(currentState.passwordError).isNotEmpty()
    }

    /**
     * GIVEN: Credenciales válidas para login
     * WHEN: Se ejecuta login
     * THEN: Cambia isAuthenticated a true
     */
    @Test
    fun testLogin_WithValidCredentials_AuthenticatesUser() = runTest {
        // Arrange
        val email = "user@email.com"
        val password = "password123"
        val user = User(
            id = "123",
            email = email,
            displayName = "Test User",
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockLoginUseCase(email, password) } returns flowOf(
            AuthResult.Success(user)
        )

        // Act
        authViewModel.login(email, password)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.isAuthenticated).isTrue()
        assertThat(currentState.currentUser?.email).isEqualTo(email)
        assertThat(currentState.error).isNull()
    }

    /**
     * GIVEN: Email no registrado
     * WHEN: Se intenta login
     * THEN: Muestra error y no autentica
     */
    @Test
    fun testLogin_WithUnregisteredEmail_ShowsError() = runTest {
        // Arrange
        val email = "notfound@email.com"
        val password = "password123"
        coEvery { mockLoginUseCase(email, password) } returns flowOf(
            AuthResult.Error("No existe cuenta con este email")
        )

        // Act
        authViewModel.login(email, password)
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.isAuthenticated).isFalse()
        assertThat(currentState.error).isNotNull()
        assertThat(currentState.error).contains("email")
    }

    /**
     * GIVEN: Usuario autenticado
     * WHEN: Se ejecuta logout
     * THEN: Limpia el estado de autenticación
     */
    @Test
    fun testLogout_WithAuthenticatedUser_ClearsAuthentication() = runTest {
        // Arrange
        coEvery { mockLogoutUseCase() } returns flowOf(
            AuthResult.Success(Unit)
        )

        // Act
        authViewModel.logout()
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.isAuthenticated).isFalse()
        assertThat(currentState.currentUser).isNull()
        assertThat(currentState.email).isEmpty()
    }

    /**
     * GIVEN: Se muestran errores
     * WHEN: Se llama clearErrors()
     * THEN: Todos los errores se limpian
     */
    @Test
    fun testClearErrors_RemovesAllErrors() = runTest {
        // Arrange
        authViewModel.onEmailChanged("invalid")
        authViewModel.onPasswordChanged("123")
        advanceUntilIdle()

        // Act
        authViewModel.clearErrors()
        advanceUntilIdle()

        // Assert
        val currentState = authViewModel.uiState.value
        assertThat(currentState.error).isNull()
        assertThat(currentState.emailError).isNull()
        assertThat(currentState.passwordError).isNull()
    }
}
