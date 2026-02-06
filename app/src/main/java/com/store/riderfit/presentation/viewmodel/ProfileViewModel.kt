package com.store.riderfit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.domain.model.User
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.auth.LogoutUseCase
import com.store.riderfit.domain.usecase.user.GetUserProfileUseCase
import com.store.riderfit.presentation.state.AuthUiState
import com.store.riderfit.presentation.state.UiState
import com.store.riderfit.utils.validators.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated = true,  // Inicia en true porque ProfileScreen solo se abre si está autenticado
            isLoading = true
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    // ==================== CARGA DE PERFIL ====================

    fun loadCurrentUser() {
        viewModelScope.launch {
            Log.d(TAG, "loadCurrentUser() iniciado")
            _uiState.update { it.copy(isLoading = true, error = null) }
            getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    Log.d(TAG, "Usuario obtenido: $user")
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            currentUserState = UiState.Success(user),
                            email = user.email,
                            displayName = user.displayName,
                            isAuthenticated = true,
                            isLoading = false
                        )
                    }
                } else {
                    Log.d(TAG, "No hay usuario autenticado")
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
        val error = EmailValidator.validate(email)
        _uiState.update {
            it.copy(
                isEmailValid = error == null,
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
                
                try {
                    val updatedUser = currentUser.copy(
                        displayName = _uiState.value.displayName,
                        updatedAt = System.currentTimeMillis()
                    )

                    // Aquí se podría guardar en Firebase/BD en el futuro
                    // Por ahora solo actualizamos el estado local
                    
                    _uiState.update {
                        it.copy(
                            currentUser = updatedUser,
                            currentUserState = UiState.Success(updatedUser),
                            isSubmitting = false,
                            error = null  // Éxito
                        )
                    }
                    
                    Log.d(TAG, "Perfil guardado: displayName=${updatedUser.displayName}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error al guardar perfil", e)
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = "Error al guardar: ${e.message}"
                        )
                    }
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

    // ==================== LOGOUT ====================

    fun logout() {
        Log.d(TAG, "logout() llamado")
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            logoutUseCase().collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Logout exitoso")
                        _uiState.update {
                            it.copy(
                                currentUser = null,
                                isAuthenticated = false,
                                email = "",
                                displayName = "",
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Error en logout: ${result.message}")
                        _uiState.update {
                            it.copy(
                                error = result.message,
                                isSubmitting = false
                            )
                        }
                    }
                    is AuthResult.Loading -> {
                        // No hacer nada, ya está en isSubmitting = true
                    }
                }
            }
        }
    }
}
