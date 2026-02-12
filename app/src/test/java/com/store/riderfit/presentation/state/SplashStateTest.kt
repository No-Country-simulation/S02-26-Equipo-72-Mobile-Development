package com.store.riderfit.presentation.state

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests unitarios para SplashState
 *
 * Valida que todos los estados sealed class estén correctamente definidos
 * y que funcionen como se espera en comparaciones y casting
 */
class SplashStateTest {

    @Test
    fun `Loading state should be created correctly`() {
        // Arrange & Act
        val loadingState = SplashState.Loading

        // Assert
        assertThat(loadingState).isInstanceOf(SplashState::class.java)
        assertThat(loadingState).isEqualTo(SplashState.Loading)
    }

    @Test
    fun `ToOnboarding state should be created correctly`() {
        // Arrange & Act
        val toOnboardingState = SplashState.ToOnboarding

        // Assert
        assertThat(toOnboardingState).isInstanceOf(SplashState::class.java)
        assertThat(toOnboardingState).isEqualTo(SplashState.ToOnboarding)
    }

    @Test
    fun `ToLogin state should be created correctly`() {
        // Arrange & Act
        val toLoginState = SplashState.ToLogin

        // Assert
        assertThat(toLoginState).isInstanceOf(SplashState::class.java)
        assertThat(toLoginState).isEqualTo(SplashState.ToLogin)
    }

    @Test
    fun `ToHome state should be created correctly`() {
        // Arrange & Act
        val toHomeState = SplashState.ToHome

        // Assert
        assertThat(toHomeState).isInstanceOf(SplashState::class.java)
        assertThat(toHomeState).isEqualTo(SplashState.ToHome)
    }

    @Test
    fun `Error state should be created correctly with message`() {
        // Arrange
        val errorMessage = "Test error message"

        // Act
        val errorState = SplashState.Error(errorMessage)

        // Assert
        assertThat(errorState).isInstanceOf(SplashState::class.java)
        assertThat(errorState).isInstanceOf(SplashState.Error::class.java)
        assertThat(errorState.message).isEqualTo(errorMessage)
    }

    @Test
    fun `Error state should handle empty message`() {
        // Arrange & Act
        val errorState = SplashState.Error("")

        // Assert
        assertThat(errorState.message).isEmpty()
    }

    @Test
    fun `Different states should not be equal`() {
        // Arrange
        val loadingState = SplashState.Loading
        val toOnboardingState = SplashState.ToOnboarding
        val toLoginState = SplashState.ToLogin
        val toHomeState = SplashState.ToHome
        val errorState = SplashState.Error("error")

        // Assert - Ningún estado debe ser igual a otro
        assertThat(loadingState).isNotEqualTo(toOnboardingState)
        assertThat(loadingState).isNotEqualTo(toLoginState)
        assertThat(loadingState).isNotEqualTo(toHomeState)
        assertThat(loadingState).isNotEqualTo(errorState)
        assertThat(toOnboardingState).isNotEqualTo(toLoginState)
        assertThat(toOnboardingState).isNotEqualTo(toHomeState)
        assertThat(toOnboardingState).isNotEqualTo(errorState)
        assertThat(toLoginState).isNotEqualTo(toHomeState)
        assertThat(toLoginState).isNotEqualTo(errorState)
        assertThat(toHomeState).isNotEqualTo(errorState)
    }

    @Test
    fun `Two Error states with same message should be equal`() {
        // Arrange
        val message = "Same error message"
        val errorState1 = SplashState.Error(message)
        val errorState2 = SplashState.Error(message)

        // Assert
        assertThat(errorState1).isEqualTo(errorState2)
        assertThat(errorState1.message).isEqualTo(errorState2.message)
    }

    @Test
    fun `Two Error states with different messages should not be equal`() {
        // Arrange
        val errorState1 = SplashState.Error("Message 1")
        val errorState2 = SplashState.Error("Message 2")

        // Assert
        assertThat(errorState1).isNotEqualTo(errorState2)
        assertThat(errorState1.message).isNotEqualTo(errorState2.message)
    }

    @Test
    fun `When pattern matching should work correctly`() {
        // Arrange
        val states = listOf(
            SplashState.Loading,
            SplashState.ToOnboarding,
            SplashState.ToLogin,
            SplashState.ToHome,
            SplashState.Error("Test error")
        )

        // Act & Assert
        states.forEach { state ->
            when (state) {
                is SplashState.Loading -> assertThat(state).isEqualTo(SplashState.Loading)
                is SplashState.ToOnboarding -> assertThat(state).isEqualTo(SplashState.ToOnboarding)
                is SplashState.ToLogin -> assertThat(state).isEqualTo(SplashState.ToLogin)
                is SplashState.ToHome -> assertThat(state).isEqualTo(SplashState.ToHome)
                is SplashState.Error -> assertThat(state.message).isEqualTo("Test error")
            }
        }
    }

    @Test
    fun `State should have proper toString representation`() {
        // Arrange & Act
        val loadingState = SplashState.Loading
        val toOnboardingState = SplashState.ToOnboarding
        val toLoginState = SplashState.ToLogin
        val toHomeState = SplashState.ToHome
        val errorState = SplashState.Error("Test message")

        // Assert - Verificar que toString no sea nulo y contenga información útil
        assertThat(loadingState.toString()).isNotEmpty()
        assertThat(loadingState.toString()).contains("Loading")

        assertThat(toOnboardingState.toString()).isNotEmpty()
        assertThat(toOnboardingState.toString()).contains("ToOnboarding")

        assertThat(toLoginState.toString()).isNotEmpty()
        assertThat(toLoginState.toString()).contains("ToLogin")

        assertThat(toHomeState.toString()).isNotEmpty()
        assertThat(toHomeState.toString()).contains("ToHome")

        assertThat(errorState.toString()).isNotEmpty()
        assertThat(errorState.toString()).contains("Error")
        assertThat(errorState.toString()).contains("Test message")
    }
}
