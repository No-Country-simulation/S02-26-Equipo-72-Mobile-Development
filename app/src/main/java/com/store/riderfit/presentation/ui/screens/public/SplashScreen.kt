package com.store.riderfit.presentation.ui.screens.public

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.R
import com.store.riderfit.presentation.state.SplashState
import com.store.riderfit.presentation.ui.navigation.Route
import com.store.riderfit.presentation.ui.theme.RiderFitColors
import com.store.riderfit.presentation.viewmodel.SplashScreenViewModel
import kotlinx.coroutines.delay

private const val LOGO_ANIMATION_DURATION_MS = 800
private const val SPINNER_FADE_OUT_DELAY_MS = 1500
private const val NAVIGATION_DELAY_MS = 2000

/**
 * Pantalla de Splash de RiderFit
 *
 * Composicion:
 * - Fondo: Imagen con overlay verde oscuro (#3A5500)
 * - Logo: SVG caballo blanco (150x150 dp) con fade-in + scale
 * - Textos: "RiderFit" + "Equipo ecuestre"
 * - Spinner: Circular blanco, fade-out después de 1.5s
 * - Navegación automática a Welcome o Home después de 2s
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val splashState = viewModel.splashState.collectAsState().value
    val showSpinner = remember { mutableStateOf(true) }

    // Log del estado actual para debug
    android.util.Log.d("SplashScreen", "Estado actual: $splashState")

    // Control del spinner: desaparece después de 1.5 segundos
    LaunchedEffect(Unit) {
        delay(SPINNER_FADE_OUT_DELAY_MS.toLong())
        showSpinner.value = false
    }

    // Efecto: Navegar después de 2 segundos según el estado
    when (splashState) {
        is SplashState.ToHome -> {
            LaunchedEffect(Unit) {
                android.util.Log.d(
                    "SplashScreen",
                    "Estado ToHome detectado, navegando a Home en ${NAVIGATION_DELAY_MS}ms"
                )
                delay(NAVIGATION_DELAY_MS.toLong())
                android.util.Log.d("SplashScreen", "Navegando a Home: ${Route.Home.route}")
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        is SplashState.ToOnboarding -> {
            LaunchedEffect(Unit) {
                android.util.Log.d(
                    "SplashScreen",
                    "Estado ToOnboarding detectado, navegando a Onboarding en ${NAVIGATION_DELAY_MS}ms"
                )
                delay(NAVIGATION_DELAY_MS.toLong())
                android.util.Log.d("SplashScreen", "Navegando a Onboarding: ${Route.Onboarding.route}")
                navController.navigate(Route.Onboarding.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            }
        }

        is SplashState.ToWelcome -> {
            LaunchedEffect(Unit) {
                android.util.Log.d(
                    "SplashScreen",
                    "Estado ToWelcome detectado, navegando a Welcome en ${NAVIGATION_DELAY_MS}ms"
                )
                delay(NAVIGATION_DELAY_MS.toLong())
                android.util.Log.d("SplashScreen", "Navegando a Welcome: ${Route.Welcome.route}")
                navController.navigate(Route.Welcome.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            }
        }

        is SplashState.ToLogin -> {
            LaunchedEffect(Unit) {
                android.util.Log.d(
                    "SplashScreen",
                    "Estado ToLogin detectado, navegando a AuthGraph en ${NAVIGATION_DELAY_MS}ms"
                )
                delay(NAVIGATION_DELAY_MS.toLong())
                android.util.Log.d("SplashScreen", "Navegando a AuthGraph: ${Route.AuthGraph.route}")
                navController.navigate(Route.AuthGraph.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            }
        }

        is SplashState.Loading -> {
            android.util.Log.d("SplashScreen", "Estado Loading - mostrando UI de carga")
        }

        is SplashState.Error -> {
            android.util.Log.e("SplashScreen", "Estado Error: ${splashState.message}")
        }
    }

    // UI Principal
    SplashScreenBackground {
        SplashScreenContent(
            isLoading = showSpinner.value
        )
    }
}

/**
 * Fondo con imagen y overlay verde
 *
 * Estructura:
 * 1. Imagen de fondo (jinetes/caballos)
 * 2. Overlay verde semitransparente (#3A5500, 60% opacity)
 * 3. Contenido (logo, textos, spinner)
 */
@Composable
private fun SplashScreenBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Imagen de fondo (caballos/jinetes)
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.bg_splash_horses),
            contentDescription = "Background - Jinetes RiderFit",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay verde oscuro (SÍ semitransparente para ver la imagen)
        // opacity = 0.60f permite ver la imagen debajo con el color verde encima
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = RiderFitColors.SplashOverlayGreen.copy(alpha = 0.60f)
                )
        )

        // Contenido (logo, textos, spinner) - visible encima de todo
        content()
    }
}

/**
 * Contenido principal del splash screen
 */
@Composable
private fun SplashScreenContent(isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espacio flexible superior (aproximadamente 30% de la pantalla)
        Box(modifier = Modifier.weight(0.3f))

        // Logo con animación
        SplashLogo()

        // Espacio entre logo y textos
        Box(modifier = Modifier.padding(top = 24.dp))

        // Textos
        SplashText()

        // Spacer flexible (ocupa menos espacio para subir el spinner)
        Box(modifier = Modifier.weight(0.4f))

        // Spinner con fade-out
        SplashLoadingIndicator(isVisible = isLoading)

        // Espacio inferior
        Box(modifier = Modifier.padding(bottom = 48.dp))
    }
}

/**
 * Logo de RiderFit con animación fade-in + scale
 */
@Composable
private fun SplashLogo() {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(durationMillis = LOGO_ANIMATION_DURATION_MS)
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(durationMillis = LOGO_ANIMATION_DURATION_MS)
        )
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.ic_logo_riderfit),
            contentDescription = "RiderFit Logo",
            modifier = Modifier.size(150.dp),
            colorFilter = ColorFilter.tint(RiderFitColors.SplashLogoWhite)
        )
    }
}

/**
 * Textos: "RiderFit" y "Equipo ecuestre"
 */
@Composable
private fun SplashText() {
    Text(
        text = "RiderFit",
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = RiderFitColors.White
    )

    Text(
        text = "Equipo ecuestre",
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = RiderFitColors.White.copy(alpha = 0.8f),
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * Spinner circular con fade-out
 */
@Composable
private fun SplashLoadingIndicator(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = RiderFitColors.White,
            strokeWidth = 4.dp
        )
    }
}
