package com.store.riderfit.presentation.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.presentation.ui.components.auth.AuthButton
import com.store.riderfit.presentation.ui.components.auth.EmailField
import com.store.riderfit.presentation.ui.components.auth.PasswordField
import com.store.riderfit.presentation.ui.components.common.ErrorDialog
import com.store.riderfit.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Navegar a home después de login exitoso
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !uiState.isSubmitting) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    if (uiState.error != null) {
        ErrorDialog(
            title = "Error de autenticación",
            message = uiState.error ?: "Error desconocido",
            onDismiss = { viewModel.clearErrors() }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "RiderFit",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Subtítulo
            Text(
                text = "Inicia sesión en tu cuenta",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email field
            EmailField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                isError = (uiState.emailError?.isNotEmpty() == true),
                errorMessage = uiState.emailError ?: "",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Password field
            PasswordField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                isError = (uiState.passwordError?.isNotEmpty() == true),
                errorMessage = uiState.passwordError ?: "",
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Login button
            AuthButton(
                text = "Iniciar sesión",
                onClick = { viewModel.login(uiState.email, uiState.password) },
                isLoading = uiState.isLoading,
                enabled = uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && !uiState.isLoading,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Register link
            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
