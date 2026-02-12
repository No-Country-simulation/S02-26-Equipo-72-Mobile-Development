package com.store.riderfit.presentation.ui.screens.personalization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.riderfit.domain.model.personalization.*
import com.store.riderfit.presentation.state.PersonalizationUiEvent

/**
 * Paso 3: Pantalla de información del jinete
 * Actualizada para coincidir con el diseño de referencia
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3RiderInfoScreen(
    selectedRiderLevel: RiderLevel?,
    riderHeight: String,
    riderWeight: String,
    bootSize: String,
    helmetSize: String,
    selectedPreferences: Set<EquipmentPreference>,
    onEvent: (PersonalizationUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Spacer inicial
        Spacer(modifier = Modifier.height(8.dp))

        // Título y descripción
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "¡Ahora el jinete!",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "El equipamiento del equipo también depende de quien monta. Define tu perfil.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

        // Nivel del jinete
        RiderLevelSection(
            selectedLevel = selectedRiderLevel,
            onSelect = { level ->
                onEvent(PersonalizationUiEvent.OnRiderLevelSelected(level))
            }
        )

        // Medidas
        MeasurementsSection(
            height = riderHeight,
            weight = riderWeight,
            bootSize = bootSize,
            helmetSize = helmetSize,
            onHeightChange = { height ->
                onEvent(PersonalizationUiEvent.OnRiderHeightChanged(height))
            },
            onWeightChange = { weight ->
                onEvent(PersonalizationUiEvent.OnRiderWeightChanged(weight))
            },
            onBootSizeChange = { size ->
                onEvent(PersonalizationUiEvent.OnBootSizeChanged(size))
            },
            onHelmetSizeChange = { size ->
                onEvent(PersonalizationUiEvent.OnHelmetSizeChanged(size))
            }
        )

        // Preferencias
        PreferencesSection(
            selectedPreferences = selectedPreferences,
            onPreferenceToggle = { preference ->
                onEvent(PersonalizationUiEvent.OnPreferenceToggled(preference))
            }
        )

        // Spacer final
        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 * Sección para el nivel del jinete con radio buttons simples
 */
@Composable
private fun RiderLevelSection(
    selectedLevel: RiderLevel?,
    onSelect: (RiderLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "¿Cuál es tu nivel como jinete?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Define el nivel de soporte recomendado más.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RiderLevel.values().forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedLevel == level,
                            onClick = { onSelect(level) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLevel == level,
                        onClick = { onSelect(level) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = level.displayName,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = level.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sección de medidas con campos de texto
 */
@Composable
private fun MeasurementsSection(
    height: String,
    weight: String,
    bootSize: String,
    helmetSize: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onBootSizeChange: (String) -> Unit,
    onHelmetSizeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Medidas",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "No necesitas ser exacto.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Altura
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Altura",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = height,
                onValueChange = onHeightChange,
                placeholder = {
                    Text(
                        text = "Ej. 1,68 m",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Text(
                text = "Como aparece en la bascula",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Peso personal
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Peso personal *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                placeholder = {
                    Text(
                        text = "Ej. 65 kg",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Text(
                text = "Número del peso (libras en el medidor y que peso utilizó en la cabalgata",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Talla de botas
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Talla de botas *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = bootSize,
                onValueChange = onBootSizeChange,
                placeholder = {
                    Text(
                        text = "Ej. Talla 38 L/M",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Text(
                text = "para tener equipos donde usted necesita unas caballerías capacidad y solo",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Talla de casco
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Talla de casco *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = helmetSize,
                onValueChange = onHelmetSizeChange,
                placeholder = {
                    Text(
                        text = "Ej. 58",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                text = "Aparece el ajuste centrado de las casillas y si necesaria los metros",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Sección de preferencias con checkboxes
 */
@Composable
private fun PreferencesSection(
    selectedPreferences: Set<EquipmentPreference>,
    onPreferenceToggle: (EquipmentPreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Preferencias",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "¿Qué priorizas?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EquipmentPreference.values().forEach { preference ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedPreferences.contains(preference),
                        onCheckedChange = { onPreferenceToggle(preference) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    Text(
                        text = preference.displayName,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
