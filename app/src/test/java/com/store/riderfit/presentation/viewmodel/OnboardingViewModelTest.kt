package com.store.riderfit.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.store.riderfit.data.local.preferences.UserPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para OnboardingViewModel
 *
 * Cubre:
 * - Navegación entre páginas
 * - Estados de UI correctos
 * - Persistencia de onboarding completado
 * - Manejo de errores
 * - Validaciones de páginas
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    // Mocks
    private lateinit var mockUserPreferences: UserPreferences
    private lateinit var viewModel: OnboardingViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockUserPreferences = mockk(relaxed = true)

        // Mock default behavior
        coEvery { mockUserPreferences.hasSeenOnboarding } returns flowOf(false)
        coEvery { mockUserPreferences.setOnboardingCompleted(any()) } returns Unit

        viewModel = OnboardingViewModel(mockUserPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given & When
        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.currentPage).isEqualTo(0)
        assertThat(initialState.isAnimating).isFalse()
        assertThat(initialState.hasSeenOnboarding).isFalse()
    }

    @Test
    fun `navigateToNext should increment current page`() = runTest {
        // Given
        val initialPage = viewModel.uiState.value.currentPage

        // When
        viewModel.navigateToNext()

        // Then
        val newState = viewModel.uiState.value
        assertThat(newState.currentPage).isEqualTo(initialPage + 1)
        assertThat(newState.currentPage).isEqualTo(1)
    }

    @Test
    fun `navigateToNext from page 0 to page 1`() = runTest {
        // Given - página inicial es 0

        // When
        viewModel.navigateToNext()

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
    }

    @Test
    fun `navigateToNext from page 1 to page 2`() = runTest {
        // Given - navegar a página 1
        viewModel.navigateToNext()

        // When - navegar a página 2
        viewModel.navigateToNext()

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(2)
    }

    @Test
    fun `navigateToNext from last page should complete onboarding`() = runTest {
        // Given - navegar a la última página (página 2)
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2

        // When - navegar desde la última página
        viewModel.navigateToNext()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.hasSeenOnboarding).isTrue()
        coVerify { mockUserPreferences.setOnboardingCompleted(true) }
    }

    @Test
    fun `navigateToPrevious should decrement current page`() = runTest {
        // Given - navegar a página 1
        viewModel.navigateToNext()

        // When
        viewModel.navigateToPrevious()

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `navigateToPrevious from first page should not change page`() = runTest {
        // Given - página inicial es 0

        // When
        viewModel.navigateToPrevious()

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `navigateToPage should set correct page`() = runTest {
        // Given
        val targetPage = 2

        // When
        viewModel.navigateToPage(targetPage)

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(targetPage)
    }

    @Test
    fun `navigateToPage with invalid page should be ignored`() = runTest {
        // Given
        val initialPage = viewModel.uiState.value.currentPage

        // When - páginas inválidas
        viewModel.navigateToPage(-1)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(initialPage)

        viewModel.navigateToPage(3)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(initialPage)

        viewModel.navigateToPage(100)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(initialPage)
    }

    @Test
    fun `isFirstPage should return true for page 0`() = runTest {
        // Given & When & Then
        assertThat(viewModel.isFirstPage()).isTrue()

        // When - navegar a página 1
        viewModel.navigateToNext()

        // Then
        assertThat(viewModel.isFirstPage()).isFalse()
    }

    @Test
    fun `isLastPage should return true for page 2`() = runTest {
        // Given - navegar a la última página
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2

        // When & Then
        assertThat(viewModel.isLastPage()).isTrue()

        // When - navegar de vuelta
        viewModel.navigateToPrevious() // 2 -> 1

        // Then
        assertThat(viewModel.isLastPage()).isFalse()
    }

    @Test
    fun `getProgress should return correct progress percentage`() = runTest {
        // Given & When & Then

        // Página 0: 1/3 = 0.33...
        assertThat(viewModel.getProgress()).isWithin(0.01f).of(0.33f)

        // Página 1: 2/3 = 0.66...
        viewModel.navigateToNext()
        assertThat(viewModel.getProgress()).isWithin(0.01f).of(0.67f)

        // Página 2: 3/3 = 1.0
        viewModel.navigateToNext()
        assertThat(viewModel.getProgress()).isWithin(0.01f).of(1.0f)
    }

    @Test
    fun `skipOnboarding should complete onboarding immediately`() = runTest {
        // Given - estado inicial

        // When
        viewModel.skipOnboarding()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.hasSeenOnboarding).isTrue()
        coVerify { mockUserPreferences.setOnboardingCompleted(true) }
    }

    @Test
    fun `resetOnboarding should return to initial state`() = runTest {
        // Given - navegar a página 2 y completar
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2
        viewModel.navigateToNext() // Complete onboarding

        // When
        viewModel.resetOnboarding()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.currentPage).isEqualTo(0)
        assertThat(state.isAnimating).isFalse()
        assertThat(state.hasSeenOnboarding).isFalse()
    }

    @Test
    fun `completeOnboarding should handle UserPreferences error gracefully`() = runTest {
        // Given - mock error en UserPreferences
        coEvery { mockUserPreferences.setOnboardingCompleted(true) } throws Exception("Test error")

        // Navegar a última página
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2

        // When
        viewModel.navigateToNext() // Complete onboarding

        // Then - debería completarse a pesar del error
        val state = viewModel.uiState.value
        assertThat(state.hasSeenOnboarding).isTrue()
        coVerify { mockUserPreferences.setOnboardingCompleted(true) }
    }

    @Test
    fun `navigation should respect page boundaries`() = runTest {
        // Test navegación hacia adelante hasta el límite
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2

        // Esta llamada debería completar el onboarding, no ir a página 3
        viewModel.navigateToNext() // Complete

        assertThat(viewModel.uiState.value.hasSeenOnboarding).isTrue()
    }

    @Test
    fun `multiple navigation calls should work correctly`() = runTest {
        // Given & When - múltiples navegaciones
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToPrevious() // 1 -> 0
        viewModel.navigateToNext() // 0 -> 1
        viewModel.navigateToNext() // 1 -> 2
        viewModel.navigateToPrevious() // 2 -> 1

        // Then
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
        assertThat(viewModel.uiState.value.hasSeenOnboarding).isFalse()
    }

    @Test
    fun `navigation should update isFirstPage and isLastPage correctly`() = runTest {
        // Página 0
        assertThat(viewModel.isFirstPage()).isTrue()
        assertThat(viewModel.isLastPage()).isFalse()

        // Página 1
        viewModel.navigateToNext()
        assertThat(viewModel.isFirstPage()).isFalse()
        assertThat(viewModel.isLastPage()).isFalse()

        // Página 2
        viewModel.navigateToNext()
        assertThat(viewModel.isFirstPage()).isFalse()
        assertThat(viewModel.isLastPage()).isTrue()
    }

    @Test
    fun `direct page navigation should work with all valid pages`() = runTest {
        // Test navegación directa a cada página válida
        for (page in 0 until 3) {
            // When
            viewModel.navigateToPage(page)

            // Then
            assertThat(viewModel.uiState.value.currentPage).isEqualTo(page)
        }
    }

    @Test
    fun `progress calculation should be accurate for all pages`() = runTest {
        val expectedProgress = listOf(1f/3f, 2f/3f, 3f/3f)

        for (page in 0 until 3) {
            // When
            viewModel.navigateToPage(page)

            // Then
            assertThat(viewModel.getProgress()).isWithin(0.01f).of(expectedProgress[page])
        }
    }
}
