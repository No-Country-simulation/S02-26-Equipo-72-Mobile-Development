package com.store.riderfit.presentation.ui.screens.personalization

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.store.riderfit.R
import com.store.riderfit.presentation.state.PersonalizationUiEvent
import com.store.riderfit.presentation.viewmodel.PersonalizationViewModel

/**
 * Pantalla principal del wizard de personalización de 3 pasos
 *
 * Responsabilidades:
 * - Mostrar progreso del wizard
 * - Manejar navegación entre pasos
 * - Mostrar contenido específico de cada paso
 * - Controlar botones de navegación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizationWizardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit,
    viewModel: PersonalizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navegar a resultado cuando la personalización esté completada
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onNavigateToResult()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de caballos arriba de todo (cambia según el paso)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val imageResource = when (uiState.currentStep) {
                    1 -> R.drawable.bg_4
                    2 -> R.drawable.bg_5
                    3 -> R.drawable.bg_4 // Usar bg_4 también para paso 3
                    else -> R.drawable.bg_4 // Default
                }

                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = "Caballos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botón de navegación atrás superpuesto con fondo semitransparente
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .size(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    IconButton(
                        onClick = {
                            if (viewModel.isFirstStep()) {
                                onNavigateBack()
                            } else {
                                viewModel.onEvent(PersonalizationUiEvent.OnPreviousStep)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            }

            // Título centrado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Personalización",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Barra de progreso
            ProgressIndicator(
                currentStep = uiState.currentStep,
                progress = uiState.getProgressPercentage(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Contenido del paso actual
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                when (uiState.currentStep) {
                    1 -> Step1DisciplineScreen(
                        selectedDiscipline = uiState.selectedDiscipline,
                        customDiscipline = uiState.customDiscipline,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.fillMaxSize()
                    )

                    2 -> Step2HorseInfoScreen(
                        horseName = uiState.horseName,
                        selectedBloodType = uiState.selectedBloodType,
                        horseAge = uiState.horseAge,
                        horseHeight = uiState.horseHeight,
                        selectedConformation = uiState.selectedConformation,
                        selectedSensitivity = uiState.selectedSensitivity,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.fillMaxSize()
                    )

                    3 -> Step3RiderInfoScreen(
                        selectedRiderLevel = uiState.selectedRiderLevel,
                        riderHeight = uiState.riderHeight,
                        riderWeight = uiState.riderWeight,
                        bootSize = uiState.bootSize,
                        helmetSize = uiState.helmetSize,
                        selectedPreferences = uiState.selectedPreferences,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Botones de navegación
            NavigationButtons(
                isFirstStep = viewModel.isFirstStep(),
                isLastStep = viewModel.isLastStep(),
                canProceed = uiState.canProceedToNext,
                isLoading = uiState.isLoading,
                onPrevious = { viewModel.onEvent(PersonalizationUiEvent.OnPreviousStep) },
                onNext = { viewModel.onEvent(PersonalizationUiEvent.OnNextStep) },
                onComplete = { viewModel.onEvent(PersonalizationUiEvent.OnComplete) },
                modifier = Modifier.padding(16.dp)
            )
        }

        // Mostrar error si existe
        uiState.error?.let { errorMessage ->
            ErrorSnackbar(
                message = errorMessage,
                onDismiss = { viewModel.onEvent(PersonalizationUiEvent.ClearError) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
 * Indicador de progreso del wizard
 */
@Composable
private fun ProgressIndicator(
    currentStep: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Paso $currentStep de 3",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Botones de navegación del wizard
 */
@Composable
private fun NavigationButtons(
    isFirstStep: Boolean,
    isLastStep: Boolean,
    canProceed: Boolean,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Botón "Anterior"
        if (!isFirstStep) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anterior")
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Botón "Siguiente" o "Completar"
        Button(
            onClick = if (isLastStep) onComplete else onNext,
            enabled = canProceed && !isLoading,
            modifier = Modifier.weight(1f)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = if (isLastStep) "Ver mis resultados" else "Aceptar"
                )
            }
        }
    }
}

/**
 * Snackbar para mostrar errores
 */
@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "OK",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
