package com.store.riderfit.presentation.ui.navigation

/**
 * Sealed class para definir todas las rutas de navegación de la app.
 *
 * Estructura:
 * - Splash: Pantalla inicial que verifica autenticación
 * - AuthGraph: Rutas protegidas de autenticación (Login, Register)
 * - MainGraph: Rutas protegidas de la app (Home, Profile)
 */
sealed class Route(val route: String) {

    // ==================== TOP LEVEL ====================
    object Splash : Route("splash")

    // ==================== AUTH GRAPH ====================
    object Welcome : Route("welcome")
    object Login : Route("login")
    object Register : Route("register")

    // ==================== MAIN GRAPH ====================
    object Home : Route("home")
    object Profile : Route("profile")

    // ==================== GRAPH ROUTES ====================
    object AuthGraph : Route("auth_graph")
    object MainGraph : Route("main_graph")
}
