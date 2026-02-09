package com.store.riderfit.presentation.state

import com.google.common.truth.Truth.assertThat
import com.store.riderfit.domain.model.User
import org.junit.Test

/**
 * Tests unitarios para AuthUiState
 * Patrón: AAA (Arrange-Act-Assert)
 * Validamos: Propiedades calculadas, estados de validación, flags de formulario
 */
class AuthUiStateTest {

    /**
     * GIVEN: Estado inicial de AuthUiState
     * WHEN: Se crea una instancia por defecto
     * THEN: Todos los campos están vacíos y no válidos
     */
    @Test
    fun testDefaultState_HasExpectedInitialValues() {
        // Arrange & Act
        val state = AuthUiState()

        // Assert
        assertThat(state.email).isEmpty()
        assertThat(state.password).isEmpty()
        assertThat(state.displayName).isEmpty()
        assertThat(state.passwordConfirm).isEmpty()

        // Validaciones iniciales
        assertThat(state.isEmailValid).isFalse()
        assertThat(state.isPasswordValid).isFalse()
        assertThat(state.isDisplayNameValid).isFalse()

        // Estados
        assertThat(state.isAuthenticated).isFalse()
        assertThat(state.currentUser).isNull()
        assertThat(state.isLoading).isTrue() // Inicia en loading para splash
        assertThat(state.error).isNull()
        assertThat(state.isSubmitting).isFalse()
    }

    /**
     * GIVEN: Formulario de login válido
     * WHEN: Se calculan las propiedades
     * THEN: isLoginFormValid es true
     */
    @Test
    fun testIsLoginFormValid_WithValidData_ReturnsTrue() {
        // Arrange
        val state = AuthUiState(
            email = "user@email.com",
            password = "password123",
            isEmailValid = true,
            isPasswordValid = true,
            isSubmitting = false
        )

        // Act & Assert
        assertThat(state.isLoginFormValid).isTrue()
    }

    /**
     * GIVEN: Formulario de login con email inválido
     * WHEN: Se calcula isLoginFormValid
     * THEN: Retorna false
     */
    @Test
    fun testIsLoginFormValid_WithInvalidEmail_ReturnsFalse() {
        // Arrange
        val state = AuthUiState(
            email = "invalidemail",
            password = "password123",
            isEmailValid = false,
            isPasswordValid = true,
            isSubmitting = false
        )

        // Act & Assert
        assertThat(state.isLoginFormValid).isFalse()
    }

    /**
     * GIVEN: Formulario de login en proceso de envío
     * WHEN: Se calcula isLoginFormValid
     * THEN: Retorna false (no se puede enviar mientras está submitting)
     */
    @Test
    fun testIsLoginFormValid_WhileSubmitting_ReturnsFalse() {
        // Arrange
        val state = AuthUiState(
            email = "user@email.com",
            password = "password123",
            isEmailValid = true,
            isPasswordValid = true,
            isSubmitting = true
        )

        // Act & Assert
        assertThat(state.isLoginFormValid).isFalse()
    }

    /**
     * GIVEN: Formulario de registro válido completo
     * WHEN: Se calcula isRegisterFormValid
     * THEN: Retorna true
     */
    @Test
    fun testIsRegisterFormValid_WithValidCompleteData_ReturnsTrue() {
        // Arrange
        val password = "securePassword123"
        val state = AuthUiState(
            email = "newuser@email.com",
            password = password,
            passwordConfirm = password,
            displayName = "Juan Pérez",
            isEmailValid = true,
            isPasswordValid = true,
            isDisplayNameValid = true,
            isSubmitting = false
        )

        // Act & Assert
        assertThat(state.isRegisterFormValid).isTrue()
    }

    /**
     * GIVEN: Formulario de registro con contraseñas no coincidentes
     * WHEN: Se calcula isRegisterFormValid
     * THEN: Retorna false
     */
    @Test
    fun testIsRegisterFormValid_WithMismatchedPasswords_ReturnsFalse() {
        // Arrange
        val state = AuthUiState(
            email = "user@email.com",
            password = "password123",
            passwordConfirm = "differentpassword",
            displayName = "Usuario Prueba",
            isEmailValid = true,
            isPasswordValid = true,
            isDisplayNameValid = true,
            isSubmitting = false
        )

        // Act & Assert
        assertThat(state.isRegisterFormValid).isFalse()
    }

    /**
     * GIVEN: Formulario de registro con displayName inválido
     * WHEN: Se calcula isRegisterFormValid
     * THEN: Retorna false
     */
    @Test
    fun testIsRegisterFormValid_WithInvalidDisplayName_ReturnsFalse() {
        // Arrange
        val password = "password123"
        val state = AuthUiState(
            email = "user@email.com",
            password = password,
            passwordConfirm = password,
            displayName = "A", // Muy corto
            isEmailValid = true,
            isPasswordValid = true,
            isDisplayNameValid = false,
            isSubmitting = false
        )

        // Act & Assert
        assertThat(state.isRegisterFormValid).isFalse()
    }

    /**
     * GIVEN: Estado con usuario autenticado
     * WHEN: Se verifica la autenticación
     * THEN: isAuthenticated es true y currentUser no es null
     */
    @Test
    fun testAuthenticatedState_WithUser_IsValid() {
        // Arrange
        val user = User(
            id = "123",
            email = "user@email.com",
            displayName = "Test User",
            photoUrl = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val state = AuthUiState(
            isAuthenticated = true,
            currentUser = user,
            isLoading = false
        )

        // Act & Assert
        assertThat(state.isAuthenticated).isTrue()
        assertThat(state.currentUser).isNotNull()
        assertThat(state.currentUser?.email).isEqualTo("user@email.com")
        assertThat(state.isLoading).isFalse()
    }

    /**
     * GIVEN: Estado con errores de validación
     * WHEN: Se verifican los errores
     * THEN: Todos los errores están presentes
     */
    @Test
    fun testValidationErrors_AreStoredCorrectly() {
        // Arrange
        val emailError = "Email no válido"
        val passwordError = "Contraseña muy corta"
        val displayNameError = "Nombre requerido"
        val confirmPasswordError = "Las contraseñas no coinciden"

        val state = AuthUiState(
            emailError = emailError,
            passwordError = passwordError,
            displayNameError = displayNameError,
            confirmPasswordError = confirmPasswordError
        )

        // Act & Assert
        assertThat(state.emailError).isEqualTo(emailError)
        assertThat(state.passwordError).isEqualTo(passwordError)
        assertThat(state.displayNameError).isEqualTo(displayNameError)
        assertThat(state.confirmPasswordError).isEqualTo(confirmPasswordError)
    }

    /**
     * GIVEN: Estado con flags de UI
     * WHEN: Se verifican los flags
     * THEN: Están configurados correctamente
     */
    @Test
    fun testUiFlags_AreSetCorrectly() {
        // Arrange
        val state = AuthUiState(
            showPassword = true,
            showPasswordConfirm = false,
            isSubmitting = true
        )

        // Act & Assert
        assertThat(state.showPassword).isTrue()
        assertThat(state.showPasswordConfirm).isFalse()
        assertThat(state.isSubmitting).isTrue()
    }

    /**
     * GIVEN: Estado con diferentes combinaciones de loading y submitting
     * WHEN: Se verifican los estados
     * THEN: Son independientes entre sí
     */
    @Test
    fun testLoadingAndSubmittingStates_AreIndependent() {
        // Arrange & Act & Assert

        // Solo loading
        val loadingState = AuthUiState(isLoading = true, isSubmitting = false)
        assertThat(loadingState.isLoading).isTrue()
        assertThat(loadingState.isSubmitting).isFalse()

        // Solo submitting
        val submittingState = AuthUiState(isLoading = false, isSubmitting = true)
        assertThat(submittingState.isLoading).isFalse()
        assertThat(submittingState.isSubmitting).isTrue()

        // Ambos
        val bothState = AuthUiState(isLoading = true, isSubmitting = true)
        assertThat(bothState.isLoading).isTrue()
        assertThat(bothState.isSubmitting).isTrue()

        // Ninguno
        val neitherState = AuthUiState(isLoading = false, isSubmitting = false)
        assertThat(neitherState.isLoading).isFalse()
        assertThat(neitherState.isSubmitting).isFalse()
    }

    /**
     * GIVEN: Formulario completo pero con error general
     * WHEN: Se verifica la validez
     * THEN: Los formularios pueden ser técnicamente válidos pero tener error general
     */
    @Test
    fun testFormsCanBeValidWithGeneralError() {
        // Arrange
        val password = "password123"
        val state = AuthUiState(
            email = "user@email.com",
            password = password,
            passwordConfirm = password,
            displayName = "Test User",
            isEmailValid = true,
            isPasswordValid = true,
            isDisplayNameValid = true,
            isSubmitting = false,
            error = "Error de conexión de red" // Error general, no de validación
        )

        // Act & Assert
        assertThat(state.isLoginFormValid).isTrue() // El formulario es técnicamente válido
        assertThat(state.isRegisterFormValid).isTrue() // El formulario es técnicamente válido
        assertThat(state.error).isNotNull() // Pero hay un error general
    }

    /**
     * GIVEN: Copy de estado con modificaciones
     * WHEN: Se crea una copia con cambios
     * THEN: Solo los campos especificados cambian
     */
    @Test
    fun testStateCopy_ModifiesOnlySpecifiedFields() {
        // Arrange
        val originalState = AuthUiState(
            email = "original@email.com",
            password = "originalpass",
            displayName = "Original Name"
        )

        // Act
        val modifiedState = originalState.copy(
            email = "modified@email.com",
            isEmailValid = true
        )

        // Assert
        assertThat(modifiedState.email).isEqualTo("modified@email.com")
        assertThat(modifiedState.isEmailValid).isTrue()
        assertThat(modifiedState.password).isEqualTo("originalpass") // No cambió
        assertThat(modifiedState.displayName).isEqualTo("Original Name") // No cambió
    }
}
