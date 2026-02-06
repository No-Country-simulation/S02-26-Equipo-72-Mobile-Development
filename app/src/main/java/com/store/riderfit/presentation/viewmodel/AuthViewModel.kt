package com.store.riderfit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.auth.LoginUseCase
import com.store.riderfit.domain.usecase.auth.LogoutUseCase
import com.store.riderfit.domain.usecase.auth.RegisterUseCase
import com.store.riderfit.presentation.state.AuthUiState
import com.store.riderfit.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    // ==================== EVENTOS ====================

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
        validateEmail(email)
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
        validatePassword(password)
    }

    fun onDisplayNameChanged(displayName: String) {
        _uiState.update { it.copy(displayName = displayName) }
        validateDisplayName(displayName)
    }

    fun onPasswordConfirmChanged(passwordConfirm: String) {
        _uiState.update { it.copy(passwordConfirm = passwordConfirm) }
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun toggleShowPasswordConfirm() {
        _uiState.update { it.copy(showPasswordConfirm = !it.showPasswordConfirm) }
    }

    fun clearErrors() {
        _uiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                displayNameError = null,
                error = null
            )
        }
    }

    // ==================== VALIDACIONES ====================

    private fun validateEmail(email: String) {
        val isValid = email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
        val error = if (!isValid && email.isNotEmpty()) "Email inválido" else null
        _uiState.update {
            it.copy(
                isEmailValid = isValid,
                emailError = error
            )
        }
    }

    private fun validatePassword(password: String) {
        val isValid = password.length >= 6
        val error = if (!isValid && password.isNotEmpty()) {
            "Mínimo 6 caracteres"
        } else null
        _uiState.update {
            it.copy(
                isPasswordValid = isValid,
                passwordError = error
            )
        }
    }

    private fun validateDisplayName(displayName: String) {
        val isValid = displayName.length >= 2
        val error = if (!isValid && displayName.isNotEmpty()) {
            "Mínimo 2 caracteres"
        } else null
        _uiState.update {
            it.copy(
                isDisplayNameValid = isValid,
                displayNameError = error
            )
        }
    }

    // ==================== OPERACIONES ====================

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            loginUseCase(email, password).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                loginState = UiState.Success(result.data),
                                currentUser = result.data,
                                isAuthenticated = true,
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                loginState = UiState.Error(result.message),
                                error = result.message,
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                loginState = UiState.Loading(),
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            registerUseCase(email, password, displayName).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                registerState = UiState.Success(result.data),
                                currentUser = result.data,
                                isAuthenticated = true,
                                email = "",
                                password = "",
                                displayName = "",
                                passwordConfirm = "",
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                registerState = UiState.Error(result.message),
                                error = result.message,
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                registerState = UiState.Loading(),
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            logoutUseCase().collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                logoutState = UiState.Success(Unit),
                                currentUser = null,
                                isAuthenticated = false,
                                email = "",
                                password = "",
                                displayName = "",
                                passwordConfirm = "",
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                logoutState = UiState.Error(result.message),
                                error = result.message,
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                logoutState = UiState.Loading(),
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCurrentUserUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        currentUserState = if (user != null) {
                            UiState.Success(user)
                        } else {
                            UiState.Idle()
                        },
                        currentUser = user,
                        isAuthenticated = user != null,
                        isLoading = false
                    )
                }
            }
        }
    }
}
