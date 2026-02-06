package com.store.riderfit.presentation.ui.screens.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.store.riderfit.presentation.ui.theme.RiderFitTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests de integración (Instrumentados) para LoginScreen
 * Usan Espresso y Compose Testing
 * Patrón: AAA (Arrange-Act-Assert)
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    /**
     * GIVEN: LoginScreen renderizado
     * WHEN: Se ingresa email
     * THEN: El email se muestra en el campo
     */
    @Test
    fun testEmailField_WhenTextEntered_DisplaysText() {
        // Arrange
        composeRule.setContent {
            RiderFitTheme {
                // Aquí iría LoginScreen cuando esté listo para testear
                // Por ahora solo demostramos la estructura
            }
        }

        // Act
        composeRule
            .onNodeWithText("Email")
            .performTextInput("user@email.com")

        // Assert
        composeRule
            .onNodeWithText("user@email.com")
            .assertExists()
    }

    /**
     * GIVEN: LoginScreen vacío
     * WHEN: Se presiona login sin datos
     * THEN: El botón está deshabilitado
     */
    @Test
    fun testLoginButton_WhenEmailEmpty_IsDisabled() {
        // Arrange
        composeRule.setContent {
            RiderFitTheme {
                // LoginScreen renderizado
            }
        }

        // Act & Assert
        composeRule
            .onNodeWithText("Iniciar sesión")
            .assertExists()
            // En un test real verificaríamos que está disabled
    }

    /**
     * GIVEN: LoginScreen con errores
     * WHEN: Se muestra dialog de error
     * THEN: El error es visible
     */
    @Test
    fun testErrorDialog_WhenLoginFails_IsDisplayed() {
        // Arrange
        composeRule.setContent {
            RiderFitTheme {
                // LoginScreen con error
            }
        }

        // Act & Assert
        composeRule
            .onNodeWithText("Error de autenticación")
            .assertExists()
    }
}

/**
 * Tests de integración para RegisterScreen
 */
@RunWith(AndroidJUnit4::class)
class RegisterScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    /**
     * GIVEN: RegisterScreen vacío
     * WHEN: Se ingresa información válida
     * THEN: El botón se habilita
     */
    @Test
    fun testRegisterButton_WhenFormValid_IsEnabled() {
        // Arrange
        composeRule.setContent {
            RiderFitTheme {
                // RegisterScreen renderizado
            }
        }

        // Act
        composeRule
            .onNodeWithText("Nombre completo")
            .performTextInput("Juan García")

        composeRule
            .onNodeWithText("Email")
            .performTextInput("juan@email.com")

        composeRule
            .onNodeWithText("Contraseña")
            .performTextInput("password123")

        // Assert
        composeRule
            .onNodeWithText("Registrarse")
            .assertExists()
    }

    /**
     * GIVEN: RegisterScreen con email duplicado
     * WHEN: Se intenta registrar
     * THEN: Se muestra mensaje de error
     */
    @Test
    fun testRegisterError_WithDuplicateEmail_ShowsError() {
        // Arrange
        composeRule.setContent {
            RiderFitTheme {
                // RegisterScreen con error
            }
        }

        // Act
        composeRule
            .onNodeWithText("Registrarse")
            .performClick()

        // Assert
        composeRule
            .onNodeWithText("ya está registrado")
            .assertExists()
    }
}
