package com.store.riderfit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * - Navegar a Login o Home según el estado
 * - Manejar timeout de verificación
 * - Loguear errores sin mostrar diálogos
 */
@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        Log.d(TAG, "SplashScreenViewModel inicializado")
        verifyAuthentication()
    }

    /**
     * Verifica el estado de autenticación del usuario
     * - Si está autenticado → ToHome
     * - Si no está autenticado → ToLogin
     * - Timeout después de 3 segundos
     */
    private fun verifyAuthentication() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Iniciando verificación de autenticación...")

                // Timeout de seguridad: máximo 3 segundos
                val isAuthenticated = withTimeoutOrNull(VERIFICATION_TIMEOUT_MS) {
                    try {
                        // Usar first() en lugar de collect para obtener un valor único
                        val user = getCurrentUserUseCase().first()
                        val authenticated = user != null
                        Log.d(TAG, "Usuario obtenido: ${if (authenticated) "autenticado" else "no autenticado"}")
                        authenticated
                    } catch (e: Exception) {
                        Log.e(TAG, "Error obteniendo usuario: ${e.message}")
                        false
                    }
                } ?: run {
                    Log.w(TAG, "Timeout en verificación de autenticación")
                    false
                }

                // Navegar según resultado
                val newState = if (isAuthenticated) {
                    Log.d(TAG, "✓ Usuario autenticado → Navegando a Home")
                    SplashState.ToHome
                } else {
                    Log.d(TAG, "✗ Usuario no autenticado → Navegando a Login")
                    SplashState.ToLogin
                }

                _splashState.update { newState }

            } catch (e: Exception) {
                Log.e(TAG, "Error crítico en verifyAuthentication: ${e.message}", e)
                _splashState.update { SplashState.ToLogin }
            }
        }
    }
}
