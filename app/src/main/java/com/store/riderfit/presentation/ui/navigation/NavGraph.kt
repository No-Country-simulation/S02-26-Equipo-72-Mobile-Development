package com.store.riderfit.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.store.riderfit.presentation.ui.screens.auth.LoginScreen
import com.store.riderfit.presentation.ui.screens.auth.RegisterScreen
import com.store.riderfit.presentation.ui.screens.auth.SplashScreen
import com.store.riderfit.presentation.ui.screens.public.HomeScreen
import com.store.riderfit.presentation.ui.screens.protected.ProfileScreen

/**
 * Grafo de navegación principal de RiderFit
 * 
 * Estructura:
 * ├── SplashScreen (punto de entrada)
 * ├── AuthGraph
 * │   ├── LoginScreen
 * │   └── RegisterScreen
 * └── MainGraph
 *     ├── HomeScreen
 *     └── ProfileScreen
 */
@Composable
fun RiderFitNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        // ==================== SPLASH SCREEN ====================
        composable(Route.Splash.route) {
            SplashScreen(navController)
        }

        // ==================== AUTH GRAPH ====================
        navigation(
            route = Route.AuthGraph.route,
            startDestination = Route.Login.route
        ) {
            composable(Route.Login.route) {
                LoginScreen(navController)
            }

            composable(Route.Register.route) {
                RegisterScreen(navController)
            }
        }

        // ==================== MAIN GRAPH ====================
        navigation(
            route = Route.MainGraph.route,
            startDestination = Route.Home.route
        ) {
            composable(Route.Home.route) {
                HomeScreen(navController)
            }

            composable(Route.Profile.route) {
                ProfileScreen(navController)
            }
        }
    }
}
