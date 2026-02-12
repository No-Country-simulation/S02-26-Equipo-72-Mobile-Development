package com.store.riderfit.presentation.state

/**
 * Estado de la pantalla de Splash Screen
 *
 * Estados posibles:
 * - Loading: Verificando autenticación y onboarding
 * - ToOnboarding: Debe navegar a OnboardingScreen (usuario nuevo)
 * - ToWelcome: Debe navegar a WelcomeScreen (usuario no autenticado, ya vio onboarding)
 * - ToLogin: Debe navegar a LoginScreen (deprecated - usar ToWelcome)
 * - ToHome: Debe navegar a HomeScreen (usuario autenticado)
 */
sealed class SplashState {
    object Loading : SplashState()
    object ToOnboarding : SplashState()
    object ToWelcome : SplashState()
    object ToLogin : SplashState()
    object ToHome : SplashState()
    data class Error(val message: String) : SplashState()
}
