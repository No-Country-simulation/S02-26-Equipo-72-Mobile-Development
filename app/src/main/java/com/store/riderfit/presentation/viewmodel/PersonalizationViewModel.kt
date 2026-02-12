package com.store.riderfit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.domain.model.personalization.*
import com.store.riderfit.presentation.state.PersonalizationUiState
import com.store.riderfit.presentation.state.PersonalizationUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el wizard de personalización de 3 pasos
 *
 * Responsabilidades:
 * - Manejar estado de cada paso del wizard
 * - Validar datos de entrada en tiempo real
 * - Controlar navegación entre pasos
 * - Persistir perfil de personalización completo
 * - Manejo de errores y estados de carga
 */
@HiltViewModel
class PersonalizationViewModel @Inject constructor(
    private val userPreferences: UserPreferences
    // TODO: Inyectar PersonalizationRepository cuando esté disponible
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalizationUiState())
    val uiState: StateFlow<PersonalizationUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "PersonalizationVM"
        private const val MIN_HORSE_AGE = 1
        private const val MAX_HORSE_AGE = 35
        private const val MIN_HORSE_HEIGHT = 0.8f
        private const val MAX_HORSE_HEIGHT = 2.2f
        private const val MIN_RIDER_HEIGHT = 1.20f
        private const val MAX_RIDER_HEIGHT = 2.20f
        private const val MIN_RIDER_WEIGHT = 30f
        private const val MAX_RIDER_WEIGHT = 200f
    }

    // init {
    //     // Agregar algunos valores de ejemplo para debugging
    //     setExampleValues()
    // }

    /**
     * Agregar valores de ejemplo para facilitar testing
     */
    private fun setExampleValues() {
        _uiState.value = _uiState.value.copy(
            // Step 1 - Ejemplo
            selectedDiscipline = EquestrianDiscipline.JUMPING,

            // Step 2 - Ejemplos
            horseName = "Bakur",
            selectedBloodType = BloodType.PURE_ENGLISH,
            horseAge = "5",
            horseHeight = "1.68",
            selectedConformation = HorseConformation.SHORT,
            selectedSensitivity = SensitivityLevel.NO,

            // Step 3 - Ejemplos
            selectedRiderLevel = RiderLevel.INTERMEDIATE,
            riderHeight = "1.75",
            riderWeight = "70",
            bootSize = "42 L/M",
            helmetSize = "58",
            selectedPreferences = setOf(EquipmentPreference.EQUILIBRIUM)
        )
        updateCanProceedToNext()
    }

    /**
     * Manejar eventos del usuario
     */
    fun onEvent(event: PersonalizationUiEvent) {
        when (event) {
            // Navegación
            PersonalizationUiEvent.NavigateToStep1 -> navigateToStep(1)
            PersonalizationUiEvent.NavigateToStep2 -> navigateToStep(2)
            PersonalizationUiEvent.NavigateToStep3 -> navigateToStep(3)
            PersonalizationUiEvent.NavigateBack -> navigateToPrevious()
            PersonalizationUiEvent.OnNextStep -> navigateToNext()
            PersonalizationUiEvent.OnPreviousStep -> navigateToPrevious()
            PersonalizationUiEvent.NavigateToHome -> completePersonalization()

            // Step 1 Events
            is PersonalizationUiEvent.OnDisciplineSelected -> updateDiscipline(event.discipline)
            is PersonalizationUiEvent.OnCustomDisciplineChanged -> updateCustomDiscipline(event.customDiscipline)

            // Step 2 Events
            is PersonalizationUiEvent.OnHorseNameChanged -> updateHorseName(event.name)
            is PersonalizationUiEvent.OnBloodTypeSelected -> updateBloodType(event.bloodType)
            is PersonalizationUiEvent.OnHorseAgeChanged -> updateHorseAge(event.age)
            is PersonalizationUiEvent.OnHorseHeightChanged -> updateHorseHeight(event.height)
            is PersonalizationUiEvent.OnConformationSelected -> updateConformation(event.conformation)
            is PersonalizationUiEvent.OnSensitivitySelected -> updateSensitivity(event.sensitivity)

            // Step 3 Events
            is PersonalizationUiEvent.OnRiderLevelSelected -> updateRiderLevel(event.level)
            is PersonalizationUiEvent.OnRiderHeightChanged -> updateRiderHeight(event.height)
            is PersonalizationUiEvent.OnRiderWeightChanged -> updateRiderWeight(event.weight)
            is PersonalizationUiEvent.OnBootSizeChanged -> updateBootSize(event.size)
            is PersonalizationUiEvent.OnHelmetSizeChanged -> updateHelmetSize(event.size)
            is PersonalizationUiEvent.OnPreferenceToggled -> togglePreference(event.preference)

            // Actions
            PersonalizationUiEvent.OnComplete -> completePersonalization()
            PersonalizationUiEvent.OnRetry -> retry()
            PersonalizationUiEvent.ClearError -> clearError()
        }
    }

    // ==================== NAVEGACIÓN ====================

    private fun navigateToNext() {
        val currentState = _uiState.value
        val isValid = currentState.isCurrentStepValid()

        Log.d(TAG, "navigateToNext - Step: ${currentState.currentStep}, isValid: $isValid")

        if (!isValid) {
            logValidationDetails(currentState)
            setErrorForCurrentStep()
            return
        }

        val currentStep = currentState.currentStep
        if (currentStep < 3) {
            Log.d(TAG, "Navegando al paso ${currentStep + 1}")
            _uiState.value = _uiState.value.copy(
                currentStep = currentStep + 1,
                error = null
            )
            updateCanProceedToNext()
        } else {
            // Último paso - completar
            Log.d(TAG, "Completando personalización")
            completePersonalization()
        }
    }

    private fun navigateToPrevious() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 1) {
            _uiState.value = _uiState.value.copy(
                currentStep = currentStep - 1,
                error = null
            )
        }
    }

    private fun navigateToStep(step: Int) {
        if (step in 1..3) {
            _uiState.value = _uiState.value.copy(
                currentStep = step,
                error = null
            )
            updateCanProceedToNext()
        }
    }

    // ==================== STEP 1: DISCIPLINA ====================

    private fun updateDiscipline(discipline: EquestrianDiscipline) {
        _uiState.value = _uiState.value.copy(
            selectedDiscipline = discipline,
            customDiscipline = if (discipline != EquestrianDiscipline.OTHER) "" else _uiState.value.customDiscipline,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateCustomDiscipline(customDiscipline: String) {
        _uiState.value = _uiState.value.copy(
            customDiscipline = customDiscipline,
            error = null
        )
        updateCanProceedToNext()
    }

    // ==================== STEP 2: CABALLO ====================

    private fun updateHorseName(name: String) {
        _uiState.value = _uiState.value.copy(
            horseName = name,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateBloodType(bloodType: BloodType) {
        _uiState.value = _uiState.value.copy(
            selectedBloodType = bloodType,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateHorseAge(age: String) {
        // Validar que solo contenga números
        val filteredAge = age.filter { it.isDigit() }

        _uiState.value = _uiState.value.copy(
            horseAge = filteredAge,
            error = null
        )

        // Validar rango si es un número válido
        val ageInt = filteredAge.toIntOrNull()
        if (ageInt != null && (ageInt < MIN_HORSE_AGE || ageInt > MAX_HORSE_AGE)) {
            setError("La edad debe estar entre $MIN_HORSE_AGE y $MAX_HORSE_AGE años")
        }

        updateCanProceedToNext()
    }

    private fun updateHorseHeight(height: String) {
        // Permitir números y punto decimal
        val filteredHeight = height.filter { it.isDigit() || it == '.' }

        _uiState.value = _uiState.value.copy(
            horseHeight = filteredHeight,
            error = null
        )

        // Validar rango si es un número válido
        val heightFloat = filteredHeight.toFloatOrNull()
        if (heightFloat != null && (heightFloat < MIN_HORSE_HEIGHT || heightFloat > MAX_HORSE_HEIGHT)) {
            setError("La altura debe estar entre ${MIN_HORSE_HEIGHT}m y ${MAX_HORSE_HEIGHT}m")
        }

        updateCanProceedToNext()
    }

    private fun updateConformation(conformation: HorseConformation) {
        _uiState.value = _uiState.value.copy(
            selectedConformation = conformation,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateSensitivity(sensitivity: SensitivityLevel) {
        _uiState.value = _uiState.value.copy(
            selectedSensitivity = sensitivity,
            error = null
        )
        updateCanProceedToNext()
    }

    // ==================== STEP 3: JINETE ====================

    private fun updateRiderLevel(level: RiderLevel) {
        _uiState.value = _uiState.value.copy(
            selectedRiderLevel = level,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateRiderHeight(height: String) {
        val filteredHeight = height.filter { it.isDigit() || it == '.' }

        _uiState.value = _uiState.value.copy(
            riderHeight = filteredHeight,
            error = null
        )

        val heightFloat = filteredHeight.toFloatOrNull()
        if (heightFloat != null && (heightFloat < MIN_RIDER_HEIGHT || heightFloat > MAX_RIDER_HEIGHT)) {
            setError("La altura debe estar entre ${MIN_RIDER_HEIGHT}m y ${MAX_RIDER_HEIGHT}m")
        }

        updateCanProceedToNext()
    }

    private fun updateRiderWeight(weight: String) {
        val filteredWeight = weight.filter { it.isDigit() || it == '.' }

        _uiState.value = _uiState.value.copy(
            riderWeight = filteredWeight,
            error = null
        )

        val weightFloat = filteredWeight.toFloatOrNull()
        if (weightFloat != null && (weightFloat < MIN_RIDER_WEIGHT || weightFloat > MAX_RIDER_WEIGHT)) {
            setError("El peso debe estar entre ${MIN_RIDER_WEIGHT}kg y ${MAX_RIDER_WEIGHT}kg")
        }

        updateCanProceedToNext()
    }

    private fun updateBootSize(size: String) {
        _uiState.value = _uiState.value.copy(
            bootSize = size,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun updateHelmetSize(size: String) {
        _uiState.value = _uiState.value.copy(
            helmetSize = size,
            error = null
        )
        updateCanProceedToNext()
    }

    private fun togglePreference(preference: EquipmentPreference) {
        val currentPreferences = _uiState.value.selectedPreferences.toMutableSet()

        if (currentPreferences.contains(preference)) {
            currentPreferences.remove(preference)
        } else {
            currentPreferences.add(preference)
        }

        _uiState.value = _uiState.value.copy(
            selectedPreferences = currentPreferences,
            error = null
        )
        updateCanProceedToNext()
    }

    // ==================== UTILIDADES ====================

    private fun updateCanProceedToNext() {
        val canProceed = _uiState.value.isCurrentStepValid()
        Log.d(TAG, "updateCanProceedToNext - Step: ${_uiState.value.currentStep}, canProceed: $canProceed")
        _uiState.value = _uiState.value.copy(canProceedToNext = canProceed)
    }

    private fun completePersonalization() {
        viewModelScope.launch {
            try {
                setLoading(true)

                // TODO: Obtener el ID real del usuario
                val userId = "current_user_id"
                val userProfile = _uiState.value.toUserProfile(userId)

                if (userProfile != null) {
                    // TODO: Guardar en repository
                    // personalizationRepository.saveUserProfile(userProfile)

                    // Marcar personalización y onboarding como completados
                    userPreferences.setPersonalizationCompleted(true)
                    userPreferences.setOnboardingCompleted(true)

                    // Limpiar flag de usuario invitado si existe
                    userPreferences.setGuestUser(false)

                    Log.d(TAG, "Personalización completada - onboarding marcado como visto")

                    _uiState.value = _uiState.value.copy(
                        isCompleted = true,
                        isLoading = false
                    )
                } else {
                    setError("Error al procesar los datos de personalización")
                }

            } catch (e: Exception) {
                setError("Error al guardar la personalización: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun retry() {
        _uiState.value = PersonalizationUiState()
    }

    private fun logValidationDetails(state: PersonalizationUiState) {
        when (state.currentStep) {
            1 -> {
                Log.d(
                    TAG,
                    "Step 1 validation - selectedDiscipline: ${state.selectedDiscipline}, customDiscipline: '${state.customDiscipline}'"
                )
            }

            2 -> {
                Log.d(TAG, "Step 2 validation:")
                Log.d(TAG, "  - horseName: '${state.horseName}' (blank: ${state.horseName.isBlank()})")
                Log.d(TAG, "  - selectedBloodType: ${state.selectedBloodType}")
                Log.d(TAG, "  - horseAge: '${state.horseAge}' (toInt: ${state.horseAge.toIntOrNull()})")
                Log.d(TAG, "  - horseHeight: '${state.horseHeight}' (toFloat: ${state.horseHeight.toFloatOrNull()})")
                Log.d(TAG, "  - selectedConformation: ${state.selectedConformation}")
                Log.d(TAG, "  - selectedSensitivity: ${state.selectedSensitivity}")
            }

            3 -> {
                Log.d(TAG, "Step 3 validation:")
                Log.d(TAG, "  - selectedRiderLevel: ${state.selectedRiderLevel}")
                Log.d(TAG, "  - riderHeight: '${state.riderHeight}' (toFloat: ${state.riderHeight.toFloatOrNull()})")
                Log.d(TAG, "  - riderWeight: '${state.riderWeight}' (toFloat: ${state.riderWeight.toFloatOrNull()})")
                Log.d(TAG, "  - bootSize: '${state.bootSize}' (blank: ${state.bootSize.isBlank()})")
                Log.d(TAG, "  - helmetSize: '${state.helmetSize}' (blank: ${state.helmetSize.isBlank()})")
                Log.d(
                    TAG,
                    "  - selectedPreferences: ${state.selectedPreferences} (empty: ${state.selectedPreferences.isEmpty()})"
                )
            }
        }
    }

    private fun setErrorForCurrentStep() {
        val currentStep = _uiState.value.currentStep
        val errorMessage = when (currentStep) {
            1 -> "Por favor selecciona una disciplina"
            2 -> getStep2ValidationError()
            3 -> "Por favor completa todos los campos del jinete y selecciona al menos una preferencia"
            else -> "Por favor completa todos los campos requeridos"
        }
        setError(errorMessage)
    }

    private fun getStep2ValidationError(): String {
        val state = _uiState.value
        val missingFields = mutableListOf<String>()

        if (state.horseName.isBlank()) missingFields.add("nombre del caballo")
        if (state.selectedBloodType == null) missingFields.add("tipo de sangre")
        if (state.horseAge.isBlank() || state.horseAge.toIntOrNull() == null || state.horseAge.toInt() <= 0) {
            missingFields.add("edad válida")
        }
        if (state.horseHeight.isBlank() || state.horseHeight.toFloatOrNull() == null) {
            missingFields.add("altura válida")
        }
        if (state.selectedConformation == null) missingFields.add("tipo de dorso")
        if (state.selectedSensitivity == null) missingFields.add("sensibilidad del dorso")

        return if (missingFields.isNotEmpty()) {
            "Faltan campos: ${missingFields.joinToString(", ")}"
        } else {
            "Por favor completa todos los campos del caballo"
        }
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            error = message,
            isLoading = false
        )
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    // ==================== FUNCIONES PÚBLICAS ====================

    fun isFirstStep(): Boolean = _uiState.value.currentStep == 1
    fun isLastStep(): Boolean = _uiState.value.currentStep == 3
    fun getCurrentStep(): Int = _uiState.value.currentStep

    /**
     * Resetear wizard completo
     */
    fun reset() {
        _uiState.value = PersonalizationUiState()
    }

    /**
     * Saltar personalización
     */
    fun skipPersonalization() {
        viewModelScope.launch {
            try {
                userPreferences.setPersonalizationCompleted(true)
                _uiState.value = _uiState.value.copy(isCompleted = true)
            } catch (e: Exception) {
                setError("Error al saltar personalización")
            }
        }
    }
}
