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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.presentation.ui.components.common.Loading
import com.store.riderfit.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar snackbar cuando se guarda exitosamente
    LaunchedEffect(uiState.isSubmitting) {
        if (!uiState.isSubmitting && uiState.error == null && uiState.currentUser != null) {
            snackbarHostState.showSnackbar("Perfil guardado correctamente")
        }
    }

    // Navegar a login después de logout exitoso
    LaunchedEffect(uiState.isSubmitting, uiState.isAuthenticated) {
        // Solo navegar si: NO está en proceso de submit, NO está autenticado, y NO hay error
        if (!uiState.isSubmitting && !uiState.isAuthenticated && uiState.currentUser == null && uiState.error == null) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = false }
            }
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
        return
    }

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error al cargar perfil",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = uiState.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { viewModel.loadCurrentUser() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Reintentar")
                }
            }
        }
        return
    }

    if (!uiState.isAuthenticated) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Debes iniciar sesión",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Ir a Login")
                }
            }
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
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Email (solo lectura)
            OutlinedTextField(
                value = uiState.email,
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
                value = uiState.displayName,
                onValueChange = { viewModel.onDisplayNameChanged(it) },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                isError = (uiState.displayNameError?.isNotEmpty() == true),
                supportingText = {
                    if (uiState.displayNameError?.isNotEmpty() == true) {
                        Text(
                            text = uiState.displayNameError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true
            )

            // Fecha de creación
            if (uiState.currentUser != null) {
                Text(
                    text = "Miembro desde: ${
                        java.text.SimpleDateFormat(
                            "dd/MM/yyyy",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(uiState.currentUser.createdAt))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Botones de acción
            Button(
                onClick = { viewModel.saveProfile() },
                enabled = !uiState.isSubmitting && uiState.isDisplayNameValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(if (uiState.isSubmitting) "Guardando..." else "Guardar Cambios")
            }

            Button(
                onClick = { viewModel.cancelEdit() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }

            // Botón Logout
            Button(
                onClick = {
                    viewModel.logout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(if (uiState.isSubmitting) "Cerrando sesión..." else "Cerrar Sesión")
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
