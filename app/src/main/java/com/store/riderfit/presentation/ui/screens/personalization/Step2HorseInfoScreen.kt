package com.store.riderfit.presentation.ui.screens.personalization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.riderfit.domain.model.personalization.*
import com.store.riderfit.presentation.state.PersonalizationUiEvent

/**
 * Paso 2: Pantalla de información del caballo
 * Actualizada para coincidir con el diseño de referencia usando dropdowns
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2HorseInfoScreen(
    horseName: String,
    selectedBloodType: BloodType?,
    horseAge: String,
    horseHeight: String,
    selectedConformation: HorseConformation?,
    selectedSensitivity: SensitivityLevel?,
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
        // Spacer para asegurar que hay espacio suficiente
        Spacer(modifier = Modifier.height(8.dp))
        // Título y descripción
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tu caballo",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Cada caballo es distinto. Estos datos nos ayudan a recomendar mejor.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

        // Nombre del caballo con dropdown predefinido
        HorseNameDropdown(
            selectedName = horseName,
            onSelect = { name ->
                onEvent(PersonalizationUiEvent.OnHorseNameChanged(name))
            }
        )

        // Tipo de sangre dropdown
        BloodTypeDropdown(
            selectedBloodType = selectedBloodType,
            onSelect = { bloodType ->
                onEvent(PersonalizationUiEvent.OnBloodTypeSelected(bloodType))
            }
        )

        // Edad dropdown
        AgeDropdown(
            selectedAge = horseAge,
            onSelect = { age ->
                onEvent(PersonalizationUiEvent.OnHorseAgeChanged(age))
            }
        )

        // Conformación section
        ConformationSection(
            height = horseHeight,
            selectedConformation = selectedConformation,
            onHeightChange = { height ->
                onEvent(PersonalizationUiEvent.OnHorseHeightChanged(height))
            },
            onConformationSelect = { conformation ->
                onEvent(PersonalizationUiEvent.OnConformationSelected(conformation))
            }
        )

        // Sensibilidad del dorso
        SensitivitySection(
            selectedSensitivity = selectedSensitivity,
            onSelect = { sensitivity ->
                onEvent(PersonalizationUiEvent.OnSensitivitySelected(sensitivity))
            }
        )
    }
}

/**
 * Dropdown genérico para campos
 */
@Composable
private fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    helperText: String? = null,
    isDropdown: Boolean = true,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = value.ifBlank { placeholder },
            onValueChange = if (isDropdown) {{}} else onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            readOnly = isDropdown,
            trailingIcon = if (isDropdown) {
                {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null
        )

        helperText?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Dropdown para tipo de sangre
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BloodTypeDropdown(
    selectedBloodType: BloodType?,
    onSelect: (BloodType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Tipo de sangre",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedBloodType?.displayName ?: "Ej. Pura sangre inglés",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                BloodType.values().forEach { bloodType ->
                    DropdownMenuItem(
                        text = {
                            Text(text = bloodType.displayName)
                        },
                        onClick = {
                            onSelect(bloodType)
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Elige del tipo de ave de tu equino (grupo de equipo).",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dropdown para edad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgeDropdown(
    selectedAge: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val ageOptions = (1..30).map { "$it años" }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Edad",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selectedAge.isNotBlank()) "$selectedAge años" else "Ej. 5 años",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ageOptions.forEach { age ->
                    DropdownMenuItem(
                        text = {
                            Text(text = age)
                        },
                        onClick = {
                            onSelect(age.replace(" años", ""))
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Elige del rango del las opciones.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sección para conformación con dropdowns
 */
@Composable
private fun ConformationSection(
    height: String,
    selectedConformation: HorseConformation?,
    onHeightChange: (String) -> Unit,
    onConformationSelect: (HorseConformation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Conformación",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Es la estructura física que determina su aptitud deportiva",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Altura dropdown
        HeightDropdown(
            selectedHeight = height,
            onSelect = onHeightChange
        )

        // Dorso dropdown
        ConformationDropdown(
            selectedConformation = selectedConformation,
            onSelect = onConformationSelect
        )

        // Color dropdown
        ColorDropdown()
    }
}

/**
 * Dropdown para altura
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeightDropdown(
    selectedHeight: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val heightOptions = listOf("1,48 m", "1,50 m", "1,55 m", "1,60 m", "1,65 m", "1,68 m", "1,70 m", "1,75 m", "1,80 m")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Alzada",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selectedHeight.isNotBlank()) "$selectedHeight m" else "Ej. 1,48 m",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                heightOptions.forEach { height ->
                    DropdownMenuItem(
                        text = {
                            Text(text = height)
                        },
                        onClick = {
                            onSelect(height.replace(" m", "").replace(",", "."))
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Desde este piso hasta el grupo caballo (zona alta, lomo...",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dropdown para conformación del dorso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConformationDropdown(
    selectedConformation: HorseConformation?,
    onSelect: (HorseConformation) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Dorso",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedConformation?.displayName ?: "Ej. Corto",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                HorseConformation.values().forEach { conformation ->
                    DropdownMenuItem(
                        text = {
                            Text(text = conformation.displayName)
                        },
                        onClick = {
                            onSelect(conformation)
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Cuidal para conocer el tipo de conformación y el estilo de montura correcta",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dropdown para color (placeholder)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorDropdown(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val colorOptions = listOf("Baja", "Media", "Alta")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Color",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "Ej. Baja",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                colorOptions.forEach { color ->
                    DropdownMenuItem(
                        text = {
                            Text(text = color)
                        },
                        onClick = {
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Determina el ajuste correcto de la estructura y sensibilidad al montar",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sección para sensibilidad del dorso con radio buttons
 */
@Composable
private fun SensitivitySection(
    selectedSensitivity: SensitivityLevel?,
    onSelect: (SensitivityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Sensibilidad",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "¿Es sensible del dorso?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SensitivityLevel.values().forEach { sensitivity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedSensitivity == sensitivity,
                            onClick = { onSelect(sensitivity) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSensitivity == sensitivity,
                        onClick = { onSelect(sensitivity) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        text = sensitivity.displayName,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Spacer final para permitir scroll completo
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "* no esta seguro, elija la opción más cercana",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dropdown para nombre del caballo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorseNameDropdown(
    selectedName: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val nameOptions = listOf("Bakker", "Thunder", "Spirit", "Shadow", "Star", "Luna", "Rex", "Bella")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Nombre",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selectedName.isNotBlank()) selectedName else "Bakker",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                nameOptions.forEach { name ->
                    DropdownMenuItem(
                        text = {
                            Text(text = name)
                        },
                        onClick = {
                            onSelect(name)
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Requerimos conocer su nombre",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
