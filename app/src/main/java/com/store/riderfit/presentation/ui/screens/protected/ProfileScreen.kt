package com.store.riderfit.presentation.ui.screens.protected

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.util.Log
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.presentation.ui.components.common.Loading
import com.store.riderfit.presentation.ui.navigation.Route
import com.store.riderfit.presentation.viewmodel.AuthViewModel
import com.store.riderfit.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Rastrear si fue la carga inicial (para saber si es logout real)
    val wasAuthenticated = remember { mutableStateOf(true) }

    Log.d("ProfileScreen", "Rendering ProfileScreen: isAuthenticated=${authUiState.isAuthenticated}, isLoading=${authUiState.isLoading}, wasAuthenticated=${wasAuthenticated.value}")

    // Actualizar el estado anterior cuando termina la carga por primera vez
    LaunchedEffect(authUiState.isLoading) {
        if (!authUiState.isLoading && wasAuthenticated.value) {
            // Carga inicial completada, guardar el estado actual
            wasAuthenticated.value = authUiState.isAuthenticated
            Log.d("ProfileScreen", "Initial load done, wasAuthenticated updated to ${wasAuthenticated.value}")
        }
    }

    // Navegar a Welcome SOLO si fue un logout real (cambió de autenticado a no autenticado)
    LaunchedEffect(authUiState.isAuthenticated) {
        if (!authUiState.isSubmitting && !authUiState.isAuthenticated && wasAuthenticated.value) {
            Log.d("ProfileScreen", "Logout detected (was authenticated, now not), navigating to Welcome")
            wasAuthenticated.value = false
            navController.navigate(Route.Welcome.route) {
                popUpTo(Route.Home.route) { inclusive = false }
            }
        }
    }

    // Verificar si es usuario invitado (no autenticado pero puede acceder a la app)
    val isGuest = !authUiState.isAuthenticated

    if (authUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = if (isGuest) "Perfil de Invitado" else "Mi Perfil",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (isGuest) {
                // Vista para usuario invitado
                GuestProfileView(
                    onLoginClick = {
                        navController.navigate(Route.Login.route)
                    },
                    onRegisterClick = {
                        navController.navigate(Route.Register.route)
                    }
                )
            } else {
                // Vista para usuario autenticado
                AuthenticatedProfileView(
                    user = authUiState.currentUser,
                    email = authUiState.email,
                    displayName = authUiState.displayName,
                    isSubmitting = authUiState.isSubmitting,
                    displayNameError = authUiState.displayNameError,
                    isDisplayNameValid = authUiState.isDisplayNameValid,
                    onDisplayNameChanged = authViewModel::onDisplayNameChanged,
                    onSaveProfile = {
                        // TODO: Implementar guardar perfil
                    },
                    onCancelEdit = {
                        // TODO: Implementar cancelar edición
                    },
                    onLogout = authViewModel::logout
                )
            }
        }

        // Snackbar para mensajes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun GuestProfileView(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "👤",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Estás navegando como invitado",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Para guardar tu información y acceder a todas las funciones, puedes crear una cuenta o iniciar sesión.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Crear Cuenta")
        }

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
private fun AuthenticatedProfileView(
    user: com.store.riderfit.domain.model.User?,
    email: String,
    displayName: String,
    isSubmitting: Boolean,
    displayNameError: String?,
    isDisplayNameValid: Boolean,
    onDisplayNameChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onCancelEdit: () -> Unit,
    onLogout: () -> Unit
) {
    // Email (solo lectura)
    OutlinedTextField(
        value = email,
        onValueChange = {},
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        enabled = false,
        readOnly = true,
        singleLine = true
    )

    // Display Name (editable)
    OutlinedTextField(
        value = displayName,
        onValueChange = onDisplayNameChanged,
        label = { Text("Nombre") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        isError = (displayNameError?.isNotEmpty() == true),
        supportingText = {
            if (displayNameError?.isNotEmpty() == true) {
                Text(
                    text = displayNameError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true
    )

    // Fecha de creación
    if (user != null) {
        Text(
            text = "Miembro desde: ${
                java.text.SimpleDateFormat(
                    "dd/MM/yyyy",
                    java.util.Locale.getDefault()
                ).format(java.util.Date(user.createdAt))
            }",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    // Botones de acción
    Button(
        onClick = onSaveProfile,
        enabled = !isSubmitting && isDisplayNameValid,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(if (isSubmitting) "Guardando..." else "Guardar Cambios")
    }

    Button(
        onClick = onCancelEdit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cancelar")
    }

    // Botón Logout
    Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        ),
        enabled = !isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(if (isSubmitting) "Cerrando sesión..." else "Cerrar Sesión")
    }
}
