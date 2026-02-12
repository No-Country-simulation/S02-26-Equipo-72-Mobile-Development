package com.store.riderfit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.domain.model.AuthResult
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.domain.usecase.auth.LoginUseCase
import com.store.riderfit.domain.usecase.auth.LogoutUseCase
import com.store.riderfit.domain.usecase.auth.RegisterUseCase
import com.store.riderfit.presentation.state.AuthUiState
import com.store.riderfit.presentation.state.UiState
import com.store.riderfit.utils.validators.EmailValidator
import com.store.riderfit.utils.validators.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        Log.d(TAG, "AuthViewModel inicializado, llamando checkCurrentUser()")
        checkCurrentUser()
        setupAuthStateListener()
    }

    private fun setupAuthStateListener() {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            _isAuthenticated.value = auth.currentUser != null
            Log.d(TAG, "Auth state changed: ${auth.currentUser != null}")
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
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
        validatePasswordConfirm(passwordConfirm)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(passwordConfirm = confirmPassword) }
        validatePasswordConfirm(confirmPassword)
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
        val error = EmailValidator.validate(email)
        _uiState.update {
            it.copy(
                isEmailValid = error == null,
                emailError = error
            )
        }
    }

    private fun validatePassword(password: String) {
        val error = PasswordValidator.validate(password)
        _uiState.update {
            it.copy(
                isPasswordValid = error == null,
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

    private fun validatePasswordConfirm(passwordConfirm: String) {
        // La validación se maneja en el estado mediante confirmPasswordError
        // No necesitamos almacenar un error separado ya que se calcula dinámicamente
    }

    // ==================== OPERACIONES ====================

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            loginUseCase(email, password).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        // Guardar en UserPreferences que está logueado
                        launch {
                            userPreferences.setLoggedIn(true)
                        }
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
                        // Guardar en UserPreferences que está logueado
                        launch {
                            userPreferences.setLoggedIn(true)
                        }
                        // NO marcar onboarding como visto - el usuario registrado debe ver onboarding y personalización

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
                        // Guardar en UserPreferences que NO está logueado
                        launch {
                            userPreferences.setLoggedIn(false)
                        }
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

    /**
     * Continuar como invitado - marcar como usuario invitado para mostrar onboarding
     */
    fun continueAsGuest() {
        viewModelScope.launch {
            try {
                // Marcar como usuario invitado que necesita ver onboarding y personalización
                userPreferences.setGuestUser(true)
                userPreferences.setOnboardingCompleted(false) // Asegurar que vea onboarding
                userPreferences.setPersonalizationCompleted(false) // Asegurar que vea personalización
                Log.d(TAG, "Usuario continuó como invitado, marcado para onboarding + personalización")
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        currentUser = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al continuar como invitado", e)
                _uiState.update {
                    it.copy(error = "Error al continuar como invitado")
                }
            }
        }
    }

    /**
     * Iniciar flujo de registro - marcar para onboarding y personalización
     */
    fun startRegistrationFlow() {
        viewModelScope.launch {
            try {
                // Marcar que necesita ver onboarding y personalización después del registro
                userPreferences.setOnboardingCompleted(false)
                userPreferences.setPersonalizationCompleted(false)
                userPreferences.setGuestUser(false)
                Log.d(TAG, "Iniciado flujo de registro, marcado para onboarding + personalización")
            } catch (e: Exception) {
                Log.e(TAG, "Error iniciando flujo de registro", e)
            }
        }
    }

    /**
     * FUNCIÓN TEMPORAL PARA DESARROLLO - Limpiar preferencias de onboarding
     * TODO: Remover en producción
     */
    fun clearOnboardingForDebug() {
        viewModelScope.launch {
            try {
                userPreferences.setOnboardingCompleted(false)
                userPreferences.setGuestUser(false)
                Log.d(TAG, "DEBUG: Preferencias de onboarding limpiadas")
            } catch (e: Exception) {
                Log.e(TAG, "Error limpiando preferencias debug", e)
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            Log.d(TAG, "checkCurrentUser() iniciado - isLoading = true")
            _uiState.update { it.copy(isLoading = true) }
            try {
                getCurrentUserUseCase().collect { user ->
                    Log.d(TAG, "getCurrentUserUseCase emitió: user = $user")
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
                    Log.d(TAG, "Estado actualizado: isAuthenticated = ${user != null}, isLoading = false")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en checkCurrentUser()", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}
