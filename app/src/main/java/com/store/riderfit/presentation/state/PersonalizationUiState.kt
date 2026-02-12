package com.store.riderfit.presentation.state

import com.store.riderfit.domain.model.personalization.*

/**
 * Estado UI para el wizard de personalización
 */
data class PersonalizationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStep: Int = 1,
    val progress: PersonalizationProgress = PersonalizationProgress(),

    // Step 1: Disciplina
    val selectedDiscipline: EquestrianDiscipline? = EquestrianDiscipline.JUMPING,
    val customDiscipline: String = "",

    // Step 2: Información del caballo
    val horseName: String = "Bakker",
    val selectedBloodType: BloodType? = BloodType.PURE_ENGLISH,
    val horseAge: String = "5",
    val horseHeight: String = "1.68",
    val selectedConformation: HorseConformation? = HorseConformation.SHORT,
    val selectedSensitivity: SensitivityLevel? = SensitivityLevel.NO,

    // Step 3: Información del jinete
    val selectedRiderLevel: RiderLevel? = RiderLevel.ADVANCED,
    val riderHeight: String = "1.68",
    val riderWeight: String = "65",
    val bootSize: String = "38 L/M",
    val helmetSize: String = "58",
    val selectedPreferences: Set<EquipmentPreference> = setOf(EquipmentPreference.MORE_FREEDOM),

    // Estado de completado
    val isCompleted: Boolean = false,
    val canProceedToNext: Boolean = true
) {

    /**
     * Obtiene el porcentaje de progreso actual
     */
    fun getProgressPercentage(): Float {
        return when (currentStep) {
            1 -> 0.25f
            2 -> 0.75f
            3 -> 1.0f
            else -> 0f
        }
    }

    /**
     * Verifica si el paso actual está completo
     */
    fun isCurrentStepValid(): Boolean {
        return when (currentStep) {
            1 -> isStep1Valid()
            2 -> isStep2Valid()
            3 -> isStep3Valid()
            else -> false
        }
    }

    /**
     * Verifica si el paso 1 (disciplina) es válido
     */
    private fun isStep1Valid(): Boolean {
        return when (selectedDiscipline) {
            EquestrianDiscipline.OTHER -> customDiscipline.isNotBlank()
            null -> false
            else -> true
        }
    }

    /**
     * Verifica si el paso 2 (caballo) es válido
     */
    private fun isStep2Valid(): Boolean {
        val nameValid = horseName.isNotBlank()
        val bloodTypeValid = selectedBloodType != null
        val ageValid = horseAge.isNotBlank() && horseAge.toIntOrNull() != null && horseAge.toIntOrNull()!! > 0
        val heightValid = horseHeight.isNotBlank() && horseHeight.toFloatOrNull() != null
        val conformationValid = selectedConformation != null
        val sensitivityValid = selectedSensitivity != null

        println("Step2 Validation:")
        println("  horseName: '$horseName' -> valid: $nameValid")
        println("  selectedBloodType: $selectedBloodType -> valid: $bloodTypeValid")
        println("  horseAge: '$horseAge' -> valid: $ageValid")
        println("  horseHeight: '$horseHeight' -> valid: $heightValid")
        println("  selectedConformation: $selectedConformation -> valid: $conformationValid")
        println("  selectedSensitivity: $selectedSensitivity -> valid: $sensitivityValid")

        val isValid = nameValid && bloodTypeValid && ageValid && heightValid && conformationValid && sensitivityValid
        println("  Overall valid: $isValid")

        return isValid
    }

    /**
     * Verifica si el paso 3 (jinete) es válido
     */
    private fun isStep3Valid(): Boolean {
        return selectedRiderLevel != null &&
                riderHeight.isNotBlank() && riderHeight.toFloatOrNull() != null &&
                riderWeight.isNotBlank() && riderWeight.toFloatOrNull() != null &&
                bootSize.isNotBlank() &&
                helmetSize.isNotBlank() &&
                selectedPreferences.isNotEmpty()
    }

    /**
     * Convierte los datos del Step 1 a DisciplineInfo
     */
    fun toDisciplineInfo(): DisciplineInfo? {
        return selectedDiscipline?.let { discipline ->
            DisciplineInfo(
                discipline = discipline,
                customDiscipline = if (discipline == EquestrianDiscipline.OTHER) customDiscipline else null
            )
        }
    }

    /**
     * Convierte los datos del Step 2 a HorseInfo
     */
    fun toHorseInfo(): HorseInfo? {
        val age = horseAge.toIntOrNull()
        val height = horseHeight.toFloatOrNull()

        return if (horseName.isNotBlank() &&
                   selectedBloodType != null &&
                   age != null &&
                   height != null &&
                   selectedConformation != null &&
                   selectedSensitivity != null) {
            HorseInfo(
                name = horseName.trim(),
                bloodType = selectedBloodType,
                age = age,
                height = height,
                conformation = selectedConformation,
                sensitivity = selectedSensitivity
            )
        } else null
    }

    /**
     * Convierte los datos del Step 3 a RiderInfo
     */
    fun toRiderInfo(): RiderInfo? {
        val height = riderHeight.toFloatOrNull()
        val weight = riderWeight.toFloatOrNull()

        return if (selectedRiderLevel != null &&
                   height != null &&
                   weight != null &&
                   bootSize.isNotBlank() &&
                   helmetSize.isNotBlank()) {
            RiderInfo(
                level = selectedRiderLevel,
                height = height,
                weight = weight,
                bootSize = bootSize.trim(),
                helmetSize = helmetSize.trim(),
                preferences = selectedPreferences
            )
        } else null
    }

    /**
     * Convierte todos los datos a UserPersonalizationProfile
     */
    fun toUserProfile(userId: String): UserPersonalizationProfile? {
        val disciplineInfo = toDisciplineInfo()
        val horseInfo = toHorseInfo()
        val riderInfo = toRiderInfo()

        return if (disciplineInfo != null && horseInfo != null && riderInfo != null) {
            UserPersonalizationProfile(
                userId = userId,
                disciplineInfo = disciplineInfo,
                horseInfo = horseInfo,
                riderInfo = riderInfo
            )
        } else null
    }
}

/**
 * Estados específicos para manejar acciones del wizard
 */
sealed class PersonalizationUiEvent {
    // Navegación
    object NavigateToStep1 : PersonalizationUiEvent()
    object NavigateToStep2 : PersonalizationUiEvent()
    object NavigateToStep3 : PersonalizationUiEvent()
    object NavigateBack : PersonalizationUiEvent()
    object NavigateToHome : PersonalizationUiEvent()

    // Step 1 Events
    data class OnDisciplineSelected(val discipline: EquestrianDiscipline) : PersonalizationUiEvent()
    data class OnCustomDisciplineChanged(val customDiscipline: String) : PersonalizationUiEvent()

    // Step 2 Events
    data class OnHorseNameChanged(val name: String) : PersonalizationUiEvent()
    data class OnBloodTypeSelected(val bloodType: BloodType) : PersonalizationUiEvent()
    data class OnHorseAgeChanged(val age: String) : PersonalizationUiEvent()
    data class OnHorseHeightChanged(val height: String) : PersonalizationUiEvent()
    data class OnConformationSelected(val conformation: HorseConformation) : PersonalizationUiEvent()
    data class OnSensitivitySelected(val sensitivity: SensitivityLevel) : PersonalizationUiEvent()

    // Step 3 Events
    data class OnRiderLevelSelected(val level: RiderLevel) : PersonalizationUiEvent()
    data class OnRiderHeightChanged(val height: String) : PersonalizationUiEvent()
    data class OnRiderWeightChanged(val weight: String) : PersonalizationUiEvent()
    data class OnBootSizeChanged(val size: String) : PersonalizationUiEvent()
    data class OnHelmetSizeChanged(val size: String) : PersonalizationUiEvent()
    data class OnPreferenceToggled(val preference: EquipmentPreference) : PersonalizationUiEvent()

    // Actions
    object OnNextStep : PersonalizationUiEvent()
    object OnPreviousStep : PersonalizationUiEvent()
    object OnComplete : PersonalizationUiEvent()
    object OnRetry : PersonalizationUiEvent()
    object ClearError : PersonalizationUiEvent()
}
