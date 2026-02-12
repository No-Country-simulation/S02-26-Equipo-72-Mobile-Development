package com.store.riderfit.presentation.ui.navigation

/**
 * Sealed class para definir todas las rutas de navegación de la app.
 *
 * Estructura:
 * - Splash: Pantalla inicial que verifica autenticación
 * - AuthGraph: Rutas protegidas de autenticación (Login, Register)
 * - MainGraph: Rutas protegidas de la app (Home, Profile)
 *
 * NOTA: Los steps de personalización (Step1, Step2, Step3) NO son rutas independientes.
 * Se manejan internamente en PersonalizationWizardScreen via ViewModel.
 */
sealed class Route(val route: String) {

    // ==================== TOP LEVEL ====================
    object Splash : Route("splash")
    object Welcome : Route("welcome")
    object Onboarding : Route("onboarding")

    // ==================== AUTH GRAPH ====================
    object Login : Route("login")
    object Register : Route("register")

    // ==================== PERSONALIZATION GRAPH ====================
    object PersonalizationWizard : Route("personalization_wizard")
    object PersonalizationResult : Route("personalization_result")

    // ==================== MAIN GRAPH ====================
    object Home : Route("home")
    object Profile : Route("profile")

    // ==================== GRAPH ROUTES ====================
    object AuthGraph : Route("auth_graph")
    object MainGraph : Route("main_graph")
}
