package com.store.riderfit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.riderfit.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado del onboarding
 */
data class OnboardingUiState(
    val currentPage: Int = 0,
    val isAnimating: Boolean = false,
    val hasSeenOnboarding: Boolean = false,
    val isGuestUser: Boolean = false
)

/**
 * ViewModel para manejar el estado y lógica del onboarding
 *
 * Responsabilidades:
 * - Control de navegación entre páginas
 * - Estado de animaciones
 * - Persistir que el usuario ya vio el onboarding
 * - Coordinar transición a login/main
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        // Verificar si es usuario invitado
        viewModelScope.launch {
            try {
                val isGuest = userPreferences.isGuestUser.first()
                _uiState.value = _uiState.value.copy(isGuestUser = isGuest)
            } catch (e: Exception) {
                android.util.Log.e("OnboardingViewModel", "Error verificando usuario invitado", e)
            }
        }
    }

    companion object {
        private const val TOTAL_PAGES = 3
        private const val FIRST_PAGE = 0
        private const val LAST_PAGE = TOTAL_PAGES - 1
    }

    /**
     * Navegar a la siguiente página
     */
    fun navigateToNext() {
        val currentPage = _uiState.value.currentPage
        if (currentPage < LAST_PAGE) {
            setAnimating(true)
            _uiState.value = _uiState.value.copy(
                currentPage = currentPage + 1
            )
            setAnimating(false)
        } else {
            // Última página - completar onboarding
            completeOnboarding()
        }
    }

    /**
     * Navegar a la página anterior
     */
    fun navigateToPrevious() {
        val currentPage = _uiState.value.currentPage
        if (currentPage > FIRST_PAGE) {
            setAnimating(true)
            _uiState.value = _uiState.value.copy(
                currentPage = currentPage - 1
            )
            setAnimating(false)
        }
    }

    /**
     * Navegar directamente a una página específica
     */
    fun navigateToPage(pageIndex: Int) {
        if (pageIndex in FIRST_PAGE..LAST_PAGE) {
            setAnimating(true)
            _uiState.value = _uiState.value.copy(
                currentPage = pageIndex
            )
            setAnimating(false)
        }
    }

    /**
     * Marcar onboarding como completado
     */
    private fun completeOnboarding() {
        viewModelScope.launch {
            try {
                // NO marcar onboarding como completado aquí
                // El onboarding se marca como completado después de la personalización
                android.util.Log.d("OnboardingViewModel", "Onboarding terminado - procediendo a personalización")

                _uiState.value = _uiState.value.copy(
                    hasSeenOnboarding = true
                )
            } catch (e: Exception) {
                // Log error but continue - no queremos bloquear al usuario
                android.util.Log.e("OnboardingViewModel", "Error en onboarding completion", e)
                _uiState.value = _uiState.value.copy(
                    hasSeenOnboarding = true
                )
            }
        }
    }

    /**
     * Saltar el onboarding (si tiene opción de skip)
     */
    fun skipOnboarding() {
        completeOnboarding()
    }

    /**
     * Controlar estado de animación
     */
    private fun setAnimating(isAnimating: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnimating = isAnimating
            )
        }
    }

    /**
     * Verificar si es la primera página
     */
    fun isFirstPage(): Boolean = _uiState.value.currentPage == FIRST_PAGE

    /**
     * Verificar si es la última página
     */
    fun isLastPage(): Boolean = _uiState.value.currentPage == LAST_PAGE

    /**
     * Obtener progreso como porcentaje (0.0 - 1.0)
     */
    fun getProgress(): Float {
        return (_uiState.value.currentPage + 1).toFloat() / TOTAL_PAGES.toFloat()
    }

    /**
     * Reiniciar el onboarding (útil para testing o debugging)
     */
    fun resetOnboarding() {
        _uiState.value = OnboardingUiState()
    }
}
