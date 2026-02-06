package com.store.riderfit.presentation.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.presentation.ui.components.debug.FirebaseDebugInfo
import com.store.riderfit.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Navegar cuando el estado cambie
    LaunchedEffect(uiState.isLoading, uiState.isAuthenticated) {
        if (!uiState.isLoading) {
            delay(300)
            val destination = if (uiState.isAuthenticated) "home" else "login"
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Timeout de seguridad: máximo 3 segundos esperando
    LaunchedEffect(Unit) {
        delay(3000)
        if (uiState.isLoading) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Cargando...",
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Debug info en la esquina inferior
        FirebaseDebugInfo()
    }
}
