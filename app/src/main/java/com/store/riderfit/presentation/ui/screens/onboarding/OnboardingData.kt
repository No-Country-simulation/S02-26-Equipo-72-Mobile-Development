package com.store.riderfit.presentation.ui.screens.onboarding

import androidx.annotation.DrawableRes
import com.store.riderfit.R

/**
 * Modelo de datos para cada página del onboarding
 *
 * Representa una pantalla individual con su contenido específico
 */
data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
    val isLast: Boolean = false
)

/**
 * Contenedor de datos estático para todas las páginas del onboarding
 */
object OnboardingData {

    /**
     * Lista completa de páginas del onboarding
     * Basado en el diseño ecuestre proporcionado
     */
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.bg_1, // Jinete montando caballo
            title = "¡Elegir equipamiento no debería ser una apuesta!",
            description = "El ajuste correcto depende del caballo, del jinete y del uso real. Cuando algo no encaja, se nota en el rendimiento y en tu bolsillo."
        ),
        OnboardingPage(
            imageRes = R.drawable.bg_2, // Collage de equipamiento
            title = "¡Aquí el equipamiento se elige con datos, no al azar!",
            description = "Creamos tu perfil y el de tu caballo para recomendar productos según medidas, uso y compatibilidad, como lo haría un especialista en persona."
        ),
        OnboardingPage(
            imageRes = R.drawable.bg_3, // Jinete con smartphone
            title = "¡Comprar con la seguridad de elegir bien!",
            description = "Encuentra equipamiento que se ajusta a tu realidad, reduce errores y toma decisiones con mayor confianza desde el primer momento.",
            isLast = true
        )
    )

    /**
     * Número total de páginas
     */
    const val TOTAL_PAGES = 3

    /**
     * Duración de animaciones en milisegundos
     */
    const val ANIMATION_DURATION_MS = 300L

    /**
     * Configuración de indicadores de página
     */
    object PageIndicator {
        const val ACTIVE_WIDTH_DP = 24
        const val INACTIVE_WIDTH_DP = 8
        const val HEIGHT_DP = 8
        const val SPACING_DP = 8
    }

    /**
     * Textos de botones según la página
     */
    fun getButtonText(currentPage: Int, isBackButton: Boolean): String {
        return when {
            isBackButton && currentPage > 0 -> "Atrás"
            !isBackButton && currentPage < TOTAL_PAGES - 1 -> "Siguiente"
            !isBackButton && currentPage == TOTAL_PAGES - 1 -> "Empezar"
            else -> ""
        }
    }

    /**
     * Validación de índice de página
     */
    fun isValidPageIndex(index: Int): Boolean {
        return index in 0 until TOTAL_PAGES
    }
}
