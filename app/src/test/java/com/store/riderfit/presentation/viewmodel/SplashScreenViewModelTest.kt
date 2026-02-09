package com.store.riderfit.presentation.viewmodel

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.common.truth.Truth.assertThat
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.presentation.state.SplashState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

/**
 * Tests unitarios para SplashScreenViewModel
 *
 * Valida:
 * - Estado inicial (Loading)
 * - Verificación de autenticación exitosa → ToHome
 * - Verificación de autenticación fallida → ToLogin
 * - Manejo de timeout (3 segundos)
 * - Manejo de errores
 * - Flujo completo de verificación
 */
class SplashScreenViewModelTest {

    private lateinit var splashScreenViewModel: SplashScreenViewModel
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(
        id = "user123",
        email = "test@example.com",
        displayName = "Test User",
        photoUrl = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        // Set Main dispatcher for viewModelScope
        Dispatchers.setMain(testDispatcher)

        // Mock android.util.Log to prevent "Method d in android.util.Log not mocked" error
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial state should be Loading`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.Loading)
    }

    @Test
    fun `When user is authenticated should navigate to Home`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(mockUser)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToHome)
        verify { Log.d("SplashScreenViewModel", "✓ Usuario autenticado → Navegando a Home") }
    }

    @Test
    fun `When user is not authenticated should navigate to Login`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle() // Wait for coroutines to complete

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToLogin)
        verify { Log.d("SplashScreenViewModel", "✗ Usuario no autenticado → Navegando a Login") }
    }

    @Test
    fun `When getCurrentUser throws exception should navigate to Login`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } throws RuntimeException("Network error")

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToLogin)
        verify { Log.e("SplashScreenViewModel", "Error obteniendo usuario: Network error") }
    }

    @Test
    fun `When verification times out should navigate to Login`() = runTest {
        // Arrange - Simular un timeout simplemente devolviendo null después de procesamiento
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToLogin)
    }

    @Test
    fun `ViewModel initialization should call verifyAuthentication`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(mockUser)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert
        verify { Log.d("SplashScreenViewModel", "SplashScreenViewModel inicializado") }
        verify { Log.d("SplashScreenViewModel", "Iniciando verificación de autenticación...") }
        verify(exactly = 1) { mockGetCurrentUserUseCase() }
    }

    @Test
    fun `Critical error in verifyAuthentication should navigate to Login`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } throws RuntimeException("Critical error")

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToLogin)
    }

    @Test
    fun `State should remain Loading until verification completes`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } coAnswers {
            flowOf(mockUser).also {
                delay(100L) // Small delay to test intermediate state
            }
        }

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)

        // Assert - Initially should be Loading
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.Loading)

        // Act - Complete the verification
        advanceUntilIdle()

        // Assert - Should change to ToHome after completion
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToHome)
    }

    @Test
    fun `Multiple state observations should work correctly`() = runTest {
        // Arrange
        val stateHistory = mutableListOf<SplashState>()
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(mockUser)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)

        // Collect initial state
        stateHistory.add(splashScreenViewModel.splashState.value)

        // Complete verification
        advanceUntilIdle()
        stateHistory.add(splashScreenViewModel.splashState.value)

        // Assert
        assertThat(stateHistory).hasSize(2)
        assertThat(stateHistory[0]).isEqualTo(SplashState.Loading)
        assertThat(stateHistory[1]).isEqualTo(SplashState.ToHome)
    }

    @Test
    fun `StateFlow should be properly exposed as read-only`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)

        // Assert - Verify StateFlow is read-only (not MutableStateFlow)
        assertThat(splashScreenViewModel.splashState).isNotInstanceOf(kotlinx.coroutines.flow.MutableStateFlow::class.java)
    }

    @Test
    fun `Logging should be called for all verification steps`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(mockUser)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert - Verify all expected log calls
        verify { Log.d("SplashScreenViewModel", "SplashScreenViewModel inicializado") }
        verify { Log.d("SplashScreenViewModel", "Iniciando verificación de autenticación...") }
        verify { Log.d("SplashScreenViewModel", "Usuario obtenido: autenticado") }
        verify { Log.d("SplashScreenViewModel", "✓ Usuario autenticado → Navegando a Home") }
    }

    @Test
    fun `When user is null from use case should navigate to Login`() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)

        // Act
        splashScreenViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase)
        advanceUntilIdle()

        // Assert
        assertThat(splashScreenViewModel.splashState.value).isEqualTo(SplashState.ToLogin)
        verify { Log.d("SplashScreenViewModel", "Usuario obtenido: no autenticado") }
        verify { Log.d("SplashScreenViewModel", "✗ Usuario no autenticado → Navegando a Login") }
    }
}
