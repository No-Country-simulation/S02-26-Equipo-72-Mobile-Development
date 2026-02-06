package com.store.riderfit.presentation.state

import com.store.riderfit.domain.model.User

/**
 * Estado de autenticación para la UI
 * Maneja todos los estados relacionados con login, register y sesión
 */
data class AuthUiState(
    // Formulario
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val passwordConfirm: String = "",
    
    // Validaciones
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isDisplayNameValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val displayNameError: String? = null,
    
    // Estados de operación
    val loginState: UiState<User> = UiState.Idle(),
    val registerState: UiState<User> = UiState.Idle(),
    val logoutState: UiState<Unit> = UiState.Idle(),
    val currentUserState: UiState<User?> = UiState.Idle(),
    
    // Estado general
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = true,  // Inicia en true para que splash espere
    val error: String? = null,
    
    // UI flags
    val showPassword: Boolean = false,
    val showPasswordConfirm: Boolean = false,
    val isSubmitting: Boolean = false
) {
    val isLoginFormValid: Boolean = isEmailValid && isPasswordValid && !isSubmitting
    val isRegisterFormValid: Boolean = 
        isEmailValid && isPasswordValid && isDisplayNameValid && 
        password == passwordConfirm && !isSubmitting
}
