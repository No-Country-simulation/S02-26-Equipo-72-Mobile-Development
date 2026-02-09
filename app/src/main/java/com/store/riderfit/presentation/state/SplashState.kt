package com.store.riderfit.presentation.state

/**
 * Estado de la pantalla de Splash Screen
 * 
 * Estados posibles:
 * - Loading: Verificando autenticación
 * - ToLogin: Debe navegar a LoginScreen (usuario no autenticado)
 * - ToHome: Debe navegar a HomeScreen (usuario autenticado)
 */
sealed class SplashState {
    object Loading : SplashState()
    object ToLogin : SplashState()
    object ToHome : SplashState()
    data class Error(val message: String) : SplashState()
}
