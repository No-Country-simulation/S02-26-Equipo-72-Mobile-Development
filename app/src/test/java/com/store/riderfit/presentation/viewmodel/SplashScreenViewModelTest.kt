package com.store.riderfit.presentation.viewmodel

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.presentation.state.SplashState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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

/**
 * Tests unitarios para SplashScreenViewModel
 * Validamos: Estados del splash, navegación basada en autenticación y onboarding
 */
class SplashScreenViewModelTest {

    private lateinit var splashViewModel: SplashScreenViewModel
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val mockUserPreferences = mockk<UserPreferences>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set Main dispatcher for viewModelScope
        Dispatchers.setMain(testDispatcher)

        // Mock android.util.Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * GIVEN: Usuario autenticado existente
     * WHEN: Se inicializa el ViewModel
     * THEN: Estado cambia a ToHome
     */
    @Test
    fun testInitialization_WithAuthenticatedUser_NavigatesToHome() = runTest {
        // Arrange
        val authenticatedUser = User(
            id = "123",
            email = "user@email.com",
            displayName = "Test User",
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(authenticatedUser)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isInstanceOf(SplashState.ToHome::class.java)
    }

    /**
     * GIVEN: Usuario no autenticado y ya vio onboarding
     * WHEN: Se inicializa el ViewModel
     * THEN: Estado cambia a ToLogin
     */
    @Test
    fun testInitialization_WithUnauthenticatedUser_AndHasSeenOnboarding_NavigatesToLogin() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isInstanceOf(SplashState.ToLogin::class.java)
    }

    /**
     * GIVEN: Usuario no autenticado y NO ha visto onboarding
     * WHEN: Se inicializa el ViewModel
     * THEN: Estado cambia a ToOnboarding
     */
    @Test
    fun testInitialization_WithUnauthenticatedUser_AndHasNotSeenOnboarding_NavigatesToOnboarding() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(false)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isInstanceOf(SplashState.ToOnboarding::class.java)
    }

    /**
     * GIVEN: GetCurrentUserUseCase lanza excepción
     * WHEN: Se inicializa el ViewModel
     * THEN: Estado cambia a ToLogin como fallback
     */
    @Test
    fun testInitialization_WithException_NavigatesToLogin() = runTest {
        // Arrange
        val errorMessage = "Error de conexión"
        coEvery { mockGetCurrentUserUseCase() } throws Exception(errorMessage)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        // En caso de error, el ViewModel navega a ToLogin como fallback
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isEqualTo(SplashState.ToLogin)
    }

    /**
     * GIVEN: UserPreferences lanza excepción
     * WHEN: Se inicializa el ViewModel con usuario no autenticado
     * THEN: Estado cambia a ToLogin por el catch general
     */
    @Test
    fun testInitialization_WithUserPreferencesException_NavigatesToLogin() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)
        coEvery { mockUserPreferences.hasSeenOnboarding } throws Exception("Preferences error")

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        // La excepción se propaga y es capturada por el catch general → ToLogin
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isEqualTo(SplashState.ToLogin)
    }

    /**
     * GIVEN: Estado inicial
     * WHEN: Se inicializa el ViewModel
     * THEN: Empieza en Loading
     */
    @Test
    fun testInitialization_StartsInLoadingState() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(null)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(false)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)

        // Assert (inicialmente debería estar en Loading)
        val initialState = splashViewModel.splashState.value
        assertThat(initialState).isInstanceOf(SplashState.Loading::class.java)
    }

    /**
     * GIVEN: Usuario con datos parciales
     * WHEN: Se autentica
     * THEN: Navega a Home con los datos disponibles
     */
    @Test
    fun testInitialization_WithPartialUserData_NavigatesToHome() = runTest {
        // Arrange
        val userWithPartialData = User(
            id = "456",
            email = "partial@email.com",
            displayName = "", // Datos parciales (displayName vacío)
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { mockGetCurrentUserUseCase() } returns flowOf(userWithPartialData)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isInstanceOf(SplashState.ToHome::class.java)
    }

    /**
     * GIVEN: Timeout en la verificación de usuario
     * WHEN: Se agota el tiempo
     * THEN: Estado cambia a ToLogin como fallback
     */
    @Test
    fun testInitialization_WithTimeout_NavigatesToLogin() = runTest {
        // Arrange
        coEvery { mockGetCurrentUserUseCase() } throws Exception("Timeout")
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        // En caso de timeout, el ViewModel va a ToLogin como fallback
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isEqualTo(SplashState.ToLogin)
    }

    /**
     * GIVEN: Múltiples llamadas al use case
     * WHEN: Se emiten diferentes estados
     * THEN: Solo el último estado es válido
     */
    @Test
    fun testInitialization_WithMultipleEmissions_UsesLatestState() = runTest {
        // Arrange
        val user1 = User(
            id = "1", email = "user1@email.com", displayName = "User 1",
            photoUrl = null, createdAt = 0L, updatedAt = 0L
        )
        val user2 = User(
            id = "2", email = "user2@email.com", displayName = "User 2",
            photoUrl = null, createdAt = 0L, updatedAt = 0L
        )

        coEvery { mockGetCurrentUserUseCase() } returns flowOf(user1, user2)
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(true)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isInstanceOf(SplashState.ToHome::class.java)
        // ToHome es un object, no contiene información del usuario
    }

    /**
     * GIVEN: Use case emite flowOf vacío (sin valores)
     * WHEN: Se inicializa
     * THEN: Va a ToLogin por el catch general
     */
    @Test
    fun testInitialization_WithEmptyFlow_NavigatesToLogin() = runTest {
        // Arrange - flowOf() vacío no emite valores, first() falla con NoSuchElementException
        coEvery { mockGetCurrentUserUseCase() } returns flowOf()
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(false)

        // Act
        splashViewModel = SplashScreenViewModel(mockGetCurrentUserUseCase, mockUserPreferences)
        advanceUntilIdle()

        // Assert
        // flowOf() vacío → first() falla → excepción se propaga al catch general → ToLogin
        val currentState = splashViewModel.splashState.value
        assertThat(currentState).isEqualTo(SplashState.ToLogin)
    }
}
