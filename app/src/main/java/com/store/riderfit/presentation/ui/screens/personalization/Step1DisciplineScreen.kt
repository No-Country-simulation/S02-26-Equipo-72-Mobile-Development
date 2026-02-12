package com.store.riderfit.presentation.ui.screens.personalization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.riderfit.R
import com.store.riderfit.domain.model.personalization.EquestrianDiscipline
import com.store.riderfit.presentation.state.PersonalizationUiEvent

/**
 * Paso 1: Pantalla de selección de disciplina ecuestre
 *
 * Diseño actualizado para coincidir con la referencia visual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1DisciplineScreen(
    selectedDiscipline: EquestrianDiscipline?,
    customDiscipline: String,
    onEvent: (PersonalizationUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {


        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Título y descripción
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "¿Qué disciplina practicas principalmente?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                )

                Text(
                    text = "Con estos datos básicos podremos mostrarte equipamiento más preciso.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            // Lista de disciplinas con radio buttons simples
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EquestrianDiscipline.values().forEach { discipline ->
                    DisciplineRadioOption(
                        discipline = discipline,
                        isSelected = selectedDiscipline == discipline,
                        onSelect = {
                            onEvent(PersonalizationUiEvent.OnDisciplineSelected(discipline))
                        }
                    )
                }
            }

            // Campo de texto personalizado para "Otra"
            AnimatedVisibility(
                visible = selectedDiscipline == EquestrianDiscipline.OTHER
            ) {
                CustomDisciplineField(
                    value = customDiscipline,
                    onValueChange = { value ->
                        onEvent(PersonalizationUiEvent.OnCustomDisciplineChanged(value))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nota informativa simple
            Text(
                text = "* El equipamiento varía según tu disciplina.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Radio button simple para cada disciplina
 */
@Composable
private fun DisciplineRadioOption(
    discipline: EquestrianDiscipline,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Text(
            text = discipline.displayName,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Campo de texto personalizado para disciplina "Otra"
 */
@Composable
private fun CustomDisciplineField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Especifica tu disciplina:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Ej: Polo, Western, Trabajo de campo...",
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
    }
}
