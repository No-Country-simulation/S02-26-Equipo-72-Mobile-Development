package com.store.riderfit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.domain.usecase.auth.GetCurrentUserUseCase
import com.store.riderfit.presentation.state.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val TAG = "SplashScreenViewModel"
private const val VERIFICATION_TIMEOUT_MS = 3000L

/**
 * ViewModel para el SplashScreen
 *
 * Responsabilidades:
 * - Verificar si el usuario está autenticado
 * - Navegar a Welcome (para usuarios no autenticados) o Home (para autenticados)
 * - Manejar timeout de verificación
 * - Loguear errores sin mostrar diálogos
 *
 * FLUJOS DE NAVEGACIÓN:
 * ✓ Usuario autenticado → Home
 * ✓ Usuario NO autenticado → Welcome (sin Onboarding automático)
 *
 * NOTA: Onboarding se muestra SOLO cuando el usuario presiona un botón específico en Welcome:
 * - "Registrarse" → Register → Onboarding
 * - "Ingresar como invitado" → Onboarding
 * - "Login" → Login → Home
 */
@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        Log.d(TAG, "SplashScreenViewModel inicializado")
        verifyAuthentication()
    }

    /**
     * Verifica el estado de autenticación del usuario
     *
     * Lógica:
     * - Si está autenticado → ToHome
     * - Si NO está autenticado → ToWelcome (sin pasar por Onboarding)
     *
     * IMPORTANTE: Onboarding NO se muestra automáticamente aquí.
     * Se muestra solo cuando el usuario presiona un botón en WelcomeScreen.
     */
    private fun verifyAuthentication() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Iniciando verificación de autenticación...")

                // Timeout de seguridad: máximo 3 segundos
                val isAuthenticated = withTimeoutOrNull(VERIFICATION_TIMEOUT_MS) {
                    try {
                        // Usar runCatching para manejar mejor las excepciones de Flow
                        val result = runCatching {
                            getCurrentUserUseCase().first()
                        }

                        val user = result.getOrNull()
                        val authenticated = user != null

                        if (result.isFailure) {
                            Log.w(TAG, "Error obteniendo usuario (manejado): ${result.exceptionOrNull()?.message}")
                        } else {
                            Log.d(TAG, "Usuario obtenido: ${if (authenticated) "autenticado" else "no autenticado"}")
                        }

                        authenticated
                    } catch (e: Exception) {
                        Log.e(TAG, "Error crítico obteniendo usuario: ${e.message}")
                        false
                    }
                } ?: run {
                    Log.w(TAG, "Timeout en verificación de autenticación")
                    false
                }

                // Determinar siguiente pantalla
                val newState = if (isAuthenticated) {
                    Log.d(TAG, "✓ Usuario autenticado → Navegando a Home")
                    SplashState.ToHome
                } else {
                    // Usuario NO autenticado → ir a Welcome
                    // Onboarding se mostrará SOLO si el usuario presiona un botón específico
                    Log.d(TAG, "✗ Usuario NO autenticado → Navegando a Welcome (sin Onboarding automático)")
                    SplashState.ToWelcome
                }

                _splashState.update { newState }

            } catch (e: Exception) {
                Log.e(TAG, "Error crítico en verifyAuthentication: ${e.message}", e)
                _splashState.update { SplashState.ToWelcome }
            }
        }
    }
}
