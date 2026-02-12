package com.store.riderfit.presentation.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.presentation.ui.components.auth.AuthButton
import com.store.riderfit.presentation.ui.components.auth.AuthButtonType
import com.store.riderfit.presentation.ui.components.auth.EmailField
import com.store.riderfit.presentation.ui.components.auth.PasswordField
import com.store.riderfit.presentation.ui.components.common.ErrorDialog
import com.store.riderfit.presentation.ui.navigation.Route
import com.store.riderfit.presentation.ui.theme.RiderFitColors
import com.store.riderfit.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onContinueAsGuest: (() -> Unit)? = null,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Navegar a home después de login exitoso
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !uiState.isSubmitting) {
            navController.navigate(Route.Home.route) {
                popUpTo(Route.Welcome.route) { inclusive = true }
                launchSingleTop = true
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón back arriba a la izquierda
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = RiderFitColors.NeutralTones.L600
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = RiderFitColors.NeutralTones.L900,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtítulo
            Text(
                text = "Inicia sesión en tu cuenta de RiderFit",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp
                ),
                color = RiderFitColors.NeutralTones.L600,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
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
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Forgot password link
            TextButton(
                onClick = { /* TODO: Implementar recuperación de contraseña */ },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RiderFitColors.Primary
                )
            }

            // Login button
            AuthButton(
                text = "Iniciar sesión",
                onClick = {
                    viewModel.login(uiState.email, uiState.password)
                },
                type = AuthButtonType.FILLED,
                isLoading = uiState.isLoading,
                enabled = uiState.email.isNotEmpty() &&
                         uiState.password.isNotEmpty() &&
                         !uiState.isLoading,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Register link
            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RiderFitColors.Primary
                )
            }

            // Continue as guest button
            onContinueAsGuest?.let { continueAsGuest ->
                TextButton(
                    onClick = {
                        viewModel.continueAsGuest()
                        continueAsGuest()
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Continuar como invitado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
