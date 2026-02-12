package com.store.riderfit.presentation.ui.screens.personalization

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.store.riderfit.R
import com.store.riderfit.presentation.state.PersonalizationUiEvent
import com.store.riderfit.presentation.viewmodel.PersonalizationViewModel
import androidx.compose.runtime.LaunchedEffect
import com.store.riderfit.data.local.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first

/**
 * Pantalla de resultados del wizard de personalización
 *
 * Muestra un resumen de la personalización completada y opciones para:
 * - Ver productos recomendados
 * - Registrarse (para usuarios invitados)
 * - Ir al home (para usuarios registrados)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    isGuestUser: Boolean = false,
    viewModel: PersonalizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var dynamicIsGuestUser by remember { mutableStateOf(isGuestUser) }

    // Detectar dinámicamente si es usuario guest desde UserPreferences
    LaunchedEffect(Unit) {
        try {
            val userPreferences = UserPreferences(context)
            dynamicIsGuestUser = userPreferences.isGuestUser.first()
        } catch (e: Exception) {
            // Si hay error, usar el valor pasado como parámetro
            dynamicIsGuestUser = isGuestUser
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de caballos arriba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_4),
                    contentDescription = "Caballos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Icono de éxito
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completado",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                // Título principal
                Text(
                    text = "¡Personalización Completada!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Descripción
                Text(
                    text = "Tu perfil de equipamiento ecuestre está listo. Ahora podrás ver recomendaciones personalizadas basadas en tu disciplina, caballo y preferencias.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                // Resumen de personalización
                PersonalizationSummaryCard(uiState = uiState)

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                if (dynamicIsGuestUser) {
                    GuestResultActions(
                        onNavigateToRegister = onNavigateToRegister,
                        onNavigateToHome = onNavigateToHome
                    )
                } else {
                    RegisteredUserResultActions(
                        onNavigateToHome = onNavigateToHome
                    )
                }
            }
        }

        // Loading overlay si es necesario
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta con resumen de la personalización
 */
@Composable
private fun PersonalizationSummaryCard(
    uiState: com.store.riderfit.presentation.state.PersonalizationUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tu Perfil Personalizado",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Disciplina
            SummaryItem(
                label = "Disciplina:",
                value = uiState.selectedDiscipline?.displayName ?: "No especificada"
            )

            // Caballo
            SummaryItem(
                label = "Caballo:",
                value = uiState.horseName.ifBlank { "Sin nombre" }
            )

            // Nivel del jinete
            SummaryItem(
                label = "Tu nivel:",
                value = uiState.selectedRiderLevel?.displayName ?: "No especificado"
            )

            // Preferencias
            val preferences = if (uiState.selectedPreferences.isNotEmpty()) {
                uiState.selectedPreferences.joinToString(", ") { it.displayName }
            } else {
                "Sin preferencias"
            }

            SummaryItem(
                label = "Preferencias:",
                value = preferences
            )
        }
    }
}

/**
 * Item individual del resumen
 */
@Composable
private fun SummaryItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Acciones para usuarios invitados
 */
@Composable
private fun GuestResultActions(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "¿Quieres guardar tu personalización?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Regístrate para guardar tu perfil y acceder a todas las funciones, o continúa explorando como invitado.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón principal - Registrarse
        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Registrarme y Guardar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Botón secundario - Continuar como invitado
        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continuar como Invitado",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

/**
 * Acciones para usuarios registrados
 */
@Composable
private fun RegisteredUserResultActions(
    onNavigateToHome: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "¡Tu perfil ha sido guardado!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ahora puedes explorar productos personalizados según tus preferencias.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón principal - Ver productos
        Button(
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Ver Productos Recomendados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
