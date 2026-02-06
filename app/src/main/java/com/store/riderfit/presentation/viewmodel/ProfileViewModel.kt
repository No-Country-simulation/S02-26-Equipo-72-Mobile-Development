package com.store.riderfit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.user.GetUserProfileUseCase
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
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    // ==================== CARGA DE PERFIL ====================

    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            currentUserState = UiState.Success(user),
                            isAuthenticated = true,
                            isLoading = false
                        )
                    }
                    loadUserProfile(user.id)
                } else {
                    _uiState.update {
                        it.copy(
                            currentUser = null,
                            currentUserState = UiState.Idle(),
                            isAuthenticated = false,
                            isLoading = false,
                            error = "No hay usuario autenticado"
                        )
                    }
                }
            }
        }
    }

    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserProfileUseCase(userId).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                currentUser = result.data,
                                currentUserState = UiState.Success(result.data),
                                displayName = result.data.displayName,
                                email = result.data.email,
                                isLoading = false
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                currentUserState = UiState.Error(result.message),
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                currentUserState = UiState.Loading(),
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== EDICIÓN DE PERFIL ====================

    fun onDisplayNameChanged(displayName: String) {
        _uiState.update { it.copy(displayName = displayName) }
        validateDisplayName(displayName)
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
        validateEmail(email)
    }

    fun validateEmail(email: String) {
        val isValid = email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
        val error = if (!isValid && email.isNotEmpty()) "Email inválido" else null
        _uiState.update {
            it.copy(
                isEmailValid = isValid,
                emailError = error
            )
        }
    }

    fun validateDisplayName(displayName: String) {
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

    fun saveProfile() {
        val currentUser = _uiState.value.currentUser
        if (currentUser != null && _uiState.value.isDisplayNameValid) {
            viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, error = null) }
                
                val updatedUser = currentUser.copy(
                    displayName = _uiState.value.displayName,
                    email = _uiState.value.email,
                    updatedAt = System.currentTimeMillis()
                )

                _uiState.update {
                    it.copy(
                        currentUser = updatedUser,
                        currentUserState = UiState.Success(updatedUser),
                        isSubmitting = false
                    )
                }
            }
        }
    }

    fun cancelEdit() {
        val currentUser = _uiState.value.currentUser
        if (currentUser != null) {
            _uiState.update {
                it.copy(
                    displayName = currentUser.displayName,
                    email = currentUser.email,
                    emailError = null,
                    displayNameError = null
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
