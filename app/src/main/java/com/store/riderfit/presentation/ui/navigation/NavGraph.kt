package com.store.riderfit.presentation.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.store.riderfit.presentation.ui.screens.auth.LoginScreen
import com.store.riderfit.presentation.ui.screens.auth.RegisterScreen
import com.store.riderfit.presentation.ui.screens.onboarding.OnboardingScreen
import com.store.riderfit.presentation.ui.screens.public.SplashScreen
import com.store.riderfit.presentation.ui.screens.public.WelcomeScreen
import com.store.riderfit.presentation.ui.screens.public.HomeScreen
import com.store.riderfit.presentation.ui.screens.protected.ProfileScreen
import com.store.riderfit.presentation.ui.screens.personalization.PersonalizationWizardScreen
import com.store.riderfit.presentation.ui.screens.personalization.ResultScreen
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.store.riderfit.presentation.ui.components.common.BottomNavBar

/**
 * Grafo de navegación principal de RiderFit
 *
 * FLUJOS PRINCIPALES DE NAVEGACIÓN:
 *
 * FLUJO 1 - INVITADO (GUEST):
 * SplashScreen
 *   ↓ (Usuario no autenticado)
 * WelcomeScreen → Botón "Invitado"
 *   ↓
 * OnboardingScreen (introducción a la app)
 *   ↓
 * PersonalizationWizardScreen (3 pasos internos):
 *   ├─ Step 1: Seleccionar disciplina
 *   ├─ Step 2: Información del caballo
 *   └─ Step 3: Información del jinete + preferencias
 *   ↓
 * ResultScreen (resumen de personalización)
 *   ├─ Botón "Registrarme y Guardar" → RegisterScreen → (vuelve a Onboarding si es necesario) → Home
 *   └─ Botón "Continuar como Invitado" → Home
 *
 * FLUJO 2 - REGISTRO (REGISTER):
 * SplashScreen
 *   ↓ (Usuario no autenticado)
 * WelcomeScreen → Botón "Crear mi cuenta"
 *   ↓
 * RegisterScreen (formulario de registro)
 *   ↓ (Registro exitoso)
 * OnboardingScreen (introducción a la app)
 *   ↓
 * PersonalizationWizardScreen (3 pasos internos):
 *   ├─ Step 1: Seleccionar disciplina
 *   ├─ Step 2: Información del caballo
 *   └─ Step 3: Información del jinete + preferencias
 *   ↓
 * ResultScreen (resumen de personalización)
 *   ↓
 * HomeScreen (productos personalizados)
 *
 * FLUJO 3 - LOGIN:
 * SplashScreen
 *   ↓ (Usuario no autenticado)
 * WelcomeScreen → Botón "Iniciar sesión"
 *   ↓
 * LoginScreen (formulario de login)
 *   ↓ (Login exitoso)
 * HomeScreen (sin pasar por Onboarding, usuario ya autenticado)
 *
 * ESTRUCTURA DE RUTAS:
 * ├── PublicScreens (pantallas públicas)
 * │   ├── SplashScreen (punto de entrada)
 * │   ├── WelcomeScreen (pantalla de bienvenida)
 * │   └── HomeScreen (home principal)
 * ├── AuthGraph (autenticación)
 * │   ├── LoginScreen (formulario de login)
 * │   └── RegisterScreen (formulario de registro)
 * ├── OnboardingScreen (solo para Guest y Register)
 * ├── PersonalizationWizard (wizard de 3 pasos - contiene Step1, Step2, Step3 como composables internos)
 * ├── PersonalizationResult (resumen de personalización)
 * └── MainGraph (pantallas protegidas de la app)
 *     └── ProfileScreen
 *
 * NOTA IMPORTANTE SOBRE LOS STEPS DE PERSONALIZACIÓN:
 * Step1DisciplineScreen, Step2HorseInfoScreen, Step3RiderInfoScreen NO son rutas de navegación.
 * Son composables internos de PersonalizationWizardScreen que se manejan via PersonalizationViewModel.
 * La navegación entre steps se controla internamente en el ViewModel, no en NavGraph.
 */
@Composable
fun RiderFitNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Solo mostramos la barra en las pantallas principales (Home y Profile)
            val showBottomBar = currentRoute in listOf(
                Route.Home.route,
                Route.Profile.route,
                Route.Search.route
            )

            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // 3. El NavHost ahora usa el innerPadding para no quedar debajo de la barra
        NavHost(
            navController = navController,
            startDestination = Route.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ==================== SPLASH SCREEN ====================
            /**
             * SplashScreen: Punto de entrada de la app
             *
             * Lógica de decisión (simple):
             * - Usuario autenticado → ToHome
             * - Usuario NO autenticado → ToWelcome (el usuario elige flujo en WelcomeScreen)
             *
             * IMPORTANTE:
             * - Onboarding NO se muestra aquí automáticamente
             * - Se muestra solo cuando el usuario presiona un botón en WelcomeScreen:
             *   1. "Registrarse" → Register → Onboarding
             *   2. "Ingresar como invitado" → Onboarding
             *   3. "Login" → Login → Home (sin Onboarding)
             */
            composable(Route.Splash.route) {
                SplashScreen(navController)
            }

            // ==================== WELCOME SCREEN ====================
            /**
             * WelcomeScreen: Pantalla de bienvenida con 3 opciones
             *
             * Opciones y destinos:
             * 1. "Crear mi cuenta" → Register (flujo REGISTRO)
             * 2. "Iniciar sesión" → Login (flujo LOGIN)
             * 3. "Invitado" → Onboarding (flujo GUEST)
             *
             * IMPORTANTE: Onboarding se muestra SOLO para Guest y Register
             * Login NO pasa por Onboarding
             */
            composable(Route.Welcome.route) {
                WelcomeScreen(navController)
            }

            // ==================== AUTH GRAPH ====================
            /**
             * AuthGraph: Contiene pantallas de autenticación
             * Punto de entrada: LoginScreen
             */
            navigation(
                route = Route.AuthGraph.route,
                startDestination = Route.Login.route
            ) {
                /**
                 * LoginScreen: Formulario de login
                 * Flujo: LOGIN
                 *
                 * Navegación post-login:
                 * - Login exitoso → Home (sin Onboarding, usuario ya autenticado)
                 * - Continuar como invitado → Home (pero marcado como guest)
                 */
                composable(Route.Login.route) {
                    LoginScreen(
                        navController = navController,
                        onContinueAsGuest = {
                            navController.navigate(Route.Home.route) {
                                popUpTo(Route.AuthGraph.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                /**
                 * RegisterScreen: Formulario de registro
                 * Flujo: REGISTER
                 *
                 * Navegación post-registro:
                 * - Registro exitoso → Onboarding (usuario debe completar onboarding + personalización)
                 */
                composable(Route.Register.route) {
                    RegisterScreen(navController)
                }
            }

            // ==================== ONBOARDING SCREEN ====================
            /**
             * OnboardingScreen: Introducción a la app
             * Flujos que acceden aquí:
             * 1. GUEST: Welcome → [Onboarding] → PersonalizationWizard
             * 2. REGISTER: Register → [Onboarding] → PersonalizationWizard
             *
             * IMPORTANTE: Login NO pasa por Onboarding
             *
             * Navegación post-onboarding:
             * - Completado → PersonalizationWizard (para ambos flujos: Guest y Register)
             */
            composable(Route.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingCompleted = { isGuestUser ->
                        // Ambos flujos (guest y register) van al wizard de personalización
                        navController.navigate(Route.PersonalizationWizard.route) {
                            popUpTo(Route.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    },
                    onContinueAsGuest = {
                        // No usado en el nuevo flujo, pero se mantiene para compatibilidad
                        navController.navigate(Route.PersonalizationWizard.route) {
                            popUpTo(Route.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // ==================== PERSONALIZATION WIZARD ====================
            /**
             * PersonalizationWizardScreen: Wizard de personalización de 3 pasos
             * Flujos que acceden aquí:
             * 1. GUEST: Onboarding → [PersonalizationWizard] → Result
             * 2. REGISTER: Onboarding → [PersonalizationWizard] → Result
             *
             * Pasos internos (NO son rutas, son composables internos):
             * - Step 1: Seleccionar disciplina ecuestre
             * - Step 2: Información del caballo
             * - Step 3: Información del jinete + preferencias
             *
             * Navegación:
             * - Entre pasos: controlada internamente por PersonalizationViewModel
             * - Post-wizard: → ResultScreen
             */
            composable(Route.PersonalizationWizard.route) {
                PersonalizationWizardScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToResult = {
                        navController.navigate(Route.PersonalizationResult.route) {
                            popUpTo(Route.PersonalizationWizard.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // ==================== PERSONALIZATION RESULT SCREEN ====================
            /**
             * ResultScreen: Pantalla de resultados de personalización
             * Flujos que acceden aquí:
             * 1. GUEST: PersonalizationWizard → [Result] → (Registrarme → Register → Home) O (Continuar como Invitado → Home)
             * 2. REGISTER: PersonalizationWizard → [Result] → Home
             *
             * Contenido:
             * - Resumen de personalización completada
             * - Icono de éxito
             * - Información del perfil personalizado
             *
             * Navegación según tipo de usuario:
             * GUEST:
             * - "Registrarme y Guardar" → Register → Onboarding (ya completado, va directamente a Home)
             * - "Continuar como Invitado" → Home (sin registrarse)
             *
             * REGISTERED:
             * - "Ver Productos Recomendados" → Home
             *
             * NOTA: El tipo de usuario (guest vs registered) se detecta dinámicamente
             * desde UserPreferences en tiempo de ejecución.
             */
            composable(Route.PersonalizationResult.route) {
                ResultScreen(
                    onNavigateToRegister = {
                        navController.navigate(Route.Register.route) {
                            popUpTo(Route.PersonalizationResult.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToHome = {
                        // Limpiar todo el back stack excepto Home para evitar problemas de navegación
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Splash.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isGuestUser = false // Se determina dinámicamente en ResultScreen via ViewModel
                )
            }

            // ==================== MAIN GRAPH ====================
            /**
             * MainGraph: Rutas protegidas de la app principal
             * Punto de entrada: HomeScreen
             *
             * IMPORTANTE: Home y Profile están en el mismo nivel dentro de MainGraph
             * para que la navegación entre ellas funcione correctamente.
             */

            navigation(
                route = Route.MainGraph.route,
                startDestination = Route.Home.route
            ) {
                // ======= RUTA DE EXPLORAR =======
                composable(Route.Search.route) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            text = "Sección Explorar\n(Próximamente)",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                            color = com.store.riderfit.presentation.ui.theme.RiderFitColors.PrimaryTones.L700
                        )
                    }
                }
                /**
                 * HomeScreen: Pantalla principal de la app (productos personalizados)
                 * Destino final de todos los flujos:
                 * 1. GUEST: Result → [Home]
                 * 2. REGISTER: Result → [Home]
                 * 3. LOGIN: Login → [Home]
                 *
                 * Contenido:
                 * - Grid de productos personalizados basado en personalización
                 * - Opción para personalizar/repersonalizar
                 * - Acceso a perfil de usuario
                 */
                composable(Route.Home.route) {
                    HomeScreen(navController)
                }

                /**
                 * ProfileScreen: Pantalla de perfil del usuario
                 * Accesible desde: HomeScreen → Botón "Perfil"
                 *
                 * Contenido:
                 * - Información del usuario autenticado
                 * - Opciones de edición de perfil
                 * - Botón de logout
                 */
                composable(Route.Profile.route) {
                    ProfileScreen(navController)

                }
            }
        }
    }
}
