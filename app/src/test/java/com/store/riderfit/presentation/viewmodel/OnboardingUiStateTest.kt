package com.store.riderfit.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests unitarios para OnboardingUiState
 *
 * Valida:
 * - Estado inicial correcto
 * - Inmutabilidad del data class
 * - Validaciones de estado
 * - Copias con modificaciones
 */
class OnboardingUiStateTest {

    @Test
    fun `default state should have correct initial values`() {
        // Given & When
        val state = OnboardingUiState()

        // Then
        assertThat(state.currentPage).isEqualTo(0)
        assertThat(state.isAnimating).isFalse()
        assertThat(state.hasSeenOnboarding).isFalse()
    }

    @Test
    fun `state should be immutable data class`() {
        // Given
        val state1 = OnboardingUiState(currentPage = 1, isAnimating = true)
        val state2 = OnboardingUiState(currentPage = 1, isAnimating = true)

        // Then
        assertThat(state1).isEqualTo(state2)
        assertThat(state1.hashCode()).isEqualTo(state2.hashCode())
    }

    @Test
    fun `copy should create new instance with modified values`() {
        // Given
        val originalState = OnboardingUiState()

        // When
        val modifiedState = originalState.copy(currentPage = 2)

        // Then
        assertThat(modifiedState.currentPage).isEqualTo(2)
        assertThat(modifiedState.isAnimating).isEqualTo(originalState.isAnimating)
        assertThat(modifiedState.hasSeenOnboarding).isEqualTo(originalState.hasSeenOnboarding)
        assertThat(modifiedState).isNotEqualTo(originalState)
    }

    @Test
    fun `copy should preserve original values for unchanged properties`() {
        // Given
        val originalState = OnboardingUiState(
            currentPage = 1,
            isAnimating = true,
            hasSeenOnboarding = false
        )

        // When
        val modifiedState = originalState.copy(hasSeenOnboarding = true)

        // Then
        assertThat(modifiedState.currentPage).isEqualTo(originalState.currentPage)
        assertThat(modifiedState.isAnimating).isEqualTo(originalState.isAnimating)
        assertThat(modifiedState.hasSeenOnboarding).isTrue()
    }

    @Test
    fun `state should support all valid page numbers`() {
        // Given & When & Then
        for (page in 0 until 3) {
            val state = OnboardingUiState(currentPage = page)
            assertThat(state.currentPage).isEqualTo(page)
            assertThat(state.currentPage).isAtLeast(0)
            assertThat(state.currentPage).isLessThan(3)
        }
    }

    @Test
    fun `state should handle animating flag correctly`() {
        // Given & When
        val animatingState = OnboardingUiState(isAnimating = true)
        val notAnimatingState = OnboardingUiState(isAnimating = false)

        // Then
        assertThat(animatingState.isAnimating).isTrue()
        assertThat(notAnimatingState.isAnimating).isFalse()
        assertThat(animatingState).isNotEqualTo(notAnimatingState)
    }

    @Test
    fun `state should handle onboarding completion flag correctly`() {
        // Given & When
        val completedState = OnboardingUiState(hasSeenOnboarding = true)
        val notCompletedState = OnboardingUiState(hasSeenOnboarding = false)

        // Then
        assertThat(completedState.hasSeenOnboarding).isTrue()
        assertThat(notCompletedState.hasSeenOnboarding).isFalse()
        assertThat(completedState).isNotEqualTo(notCompletedState)
    }

    @Test
    fun `state should support chained copy operations`() {
        // Given
        val initialState = OnboardingUiState()

        // When
        val finalState = initialState
            .copy(currentPage = 1)
            .copy(isAnimating = true)
            .copy(hasSeenOnboarding = true)

        // Then
        assertThat(finalState.currentPage).isEqualTo(1)
        assertThat(finalState.isAnimating).isTrue()
        assertThat(finalState.hasSeenOnboarding).isTrue()
    }

    @Test
    fun `different states should not be equal`() {
        // Given
        val state1 = OnboardingUiState(currentPage = 0)
        val state2 = OnboardingUiState(currentPage = 1)
        val state3 = OnboardingUiState(isAnimating = true)
        val state4 = OnboardingUiState(hasSeenOnboarding = true)

        // Then
        assertThat(state1).isNotEqualTo(state2)
        assertThat(state1).isNotEqualTo(state3)
        assertThat(state1).isNotEqualTo(state4)
        assertThat(state2).isNotEqualTo(state3)
        assertThat(state2).isNotEqualTo(state4)
        assertThat(state3).isNotEqualTo(state4)
    }

    @Test
    fun `toString should contain all properties`() {
        // Given
        val state = OnboardingUiState(
            currentPage = 2,
            isAnimating = true,
            hasSeenOnboarding = true
        )

        // When
        val toString = state.toString()

        // Then
        assertThat(toString).contains("currentPage")
        assertThat(toString).contains("2")
        assertThat(toString).contains("isAnimating")
        assertThat(toString).contains("true")
        assertThat(toString).contains("hasSeenOnboarding")
    }

    @Test
    fun `state with maximum page should be valid`() {
        // Given
        val maxPage = 2 // Última página válida

        // When
        val state = OnboardingUiState(currentPage = maxPage)

        // Then
        assertThat(state.currentPage).isEqualTo(maxPage)
    }

    @Test
    fun `state combinations should work correctly`() {
        // Given & When - diferentes combinaciones de estado
        val combinations = listOf(
            OnboardingUiState(currentPage = 0, isAnimating = false, hasSeenOnboarding = false),
            OnboardingUiState(currentPage = 1, isAnimating = true, hasSeenOnboarding = false),
            OnboardingUiState(currentPage = 2, isAnimating = false, hasSeenOnboarding = true),
            OnboardingUiState(currentPage = 0, isAnimating = true, hasSeenOnboarding = true)
        )

        // Then - todas las combinaciones deberían ser válidas
        combinations.forEach { state ->
            assertThat(state.currentPage).isAtLeast(0)
            assertThat(state.currentPage).isLessThan(3)
        }

        // Y diferentes entre sí
        for (i in combinations.indices) {
            for (j in i + 1 until combinations.size) {
                assertThat(combinations[i]).isNotEqualTo(combinations[j])
            }
        }
    }
}
