package com.store.riderfit.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Light Theme Color Scheme for RiderFit
 * Based on Material 3 Design System with tonal palettes:
 * - Primary: Green (H: 79, S: 100)
 * - Secondary: Beige/Yellow (H: 51, S: 100)
 * - Neutral: Blue-Gray (H: 198, S: 20)
 * - Error: Red (H: 359, S: 100)
 */
private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    // Primary colors
    primary = RiderFitColors.Primary,                    // L700 - Verde principal
    onPrimary = RiderFitColors.OnPrimary,                // Blanco sobre verde
    primaryContainer = RiderFitColors.PrimaryContainer,  // L50 - Container claro
    onPrimaryContainer = RiderFitColors.OnPrimaryContainer, // L900 - Texto oscuro

    // Secondary colors
    secondary = RiderFitColors.Secondary,                // L700 - Beige/amarillo principal
    onSecondary = RiderFitColors.OnSecondary,            // Blanco sobre beige
    secondaryContainer = RiderFitColors.SecondaryContainer, // L50 - Container claro
    onSecondaryContainer = RiderFitColors.OnSecondaryContainer, // L900 - Texto oscuro

    // Tertiary (usando paleta secundaria)
    tertiary = RiderFitColors.SecondaryTones.L400,       // Tono medio del beige
    onTertiary = RiderFitColors.White,
    tertiaryContainer = RiderFitColors.SecondaryTones.L100, // Container claro
    onTertiaryContainer = RiderFitColors.SecondaryTones.L900, // Texto oscuro

    // Background & Surface
    background = RiderFitColors.Background,              // Blanco puro
    onBackground = RiderFitColors.OnBackground,          // L900 - Texto principal
    surface = RiderFitColors.Surface,                    // Blanco puro
    onSurface = RiderFitColors.OnSurface,                // L900 - Texto sobre superficie
    surfaceVariant = RiderFitColors.SurfaceVariant,      // L50 - Variante clara
    onSurfaceVariant = RiderFitColors.OnSurfaceVariant,  // L600 - Texto secundario

    // Error
    error = RiderFitColors.Error,                        // L500 - Rojo principal
    onError = RiderFitColors.OnError,                    // Blanco sobre error
    errorContainer = RiderFitColors.ErrorContainer,      // L50 - Container claro
    onErrorContainer = RiderFitColors.OnErrorContainer,  // L900 - Texto oscuro

    // Outline
    outline = RiderFitColors.Outline,                    // L300 - Bordes
    outlineVariant = RiderFitColors.OutlineVariant,      // L100 - Bordes claros

    // Additional Material 3 colors
    scrim = RiderFitColors.Scrim,                        // Negro transparente
    inverseSurface = RiderFitColors.InverseSurface,      // L800 - Superficie inversa
    inverseOnSurface = RiderFitColors.InverseOnSurface,  // L50 - Texto sobre inversa
    inversePrimary = RiderFitColors.InversePrimary       // L200 - Primary invertido
)

/**
 * Dark Theme Color Scheme for RiderFit
 * Optimizado para modo oscuro con alta legibilidad
 */
private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    // Primary colors - tonos más claros para visibilidad en oscuro
    primary = RiderFitColors.PrimaryTones.L200,          // Verde claro
    onPrimary = RiderFitColors.PrimaryTones.L900,        // Verde muy oscuro
    primaryContainer = RiderFitColors.PrimaryTones.L800, // Container oscuro
    onPrimaryContainer = RiderFitColors.PrimaryTones.L100, // Texto claro

    // Secondary colors
    secondary = RiderFitColors.SecondaryTones.L200,      // Beige claro
    onSecondary = RiderFitColors.SecondaryTones.L900,    // Beige muy oscuro
    secondaryContainer = RiderFitColors.SecondaryTones.L800, // Container oscuro
    onSecondaryContainer = RiderFitColors.SecondaryTones.L100, // Texto claro

    // Tertiary
    tertiary = RiderFitColors.SecondaryTones.L300,       // Beige medio-claro
    onTertiary = RiderFitColors.SecondaryTones.L900,     // Texto oscuro
    tertiaryContainer = RiderFitColors.SecondaryTones.L700, // Container medio
    onTertiaryContainer = RiderFitColors.SecondaryTones.L50, // Texto muy claro

    // Background & Surface - tonos oscuros
    background = RiderFitColors.NeutralTones.L900,       // Fondo muy oscuro
    onBackground = RiderFitColors.NeutralTones.L100,     // Texto claro
    surface = RiderFitColors.NeutralTones.L800,          // Superficie oscura
    onSurface = RiderFitColors.NeutralTones.L50,         // Texto muy claro
    surfaceVariant = RiderFitColors.NeutralTones.L700,   // Variante oscura
    onSurfaceVariant = RiderFitColors.NeutralTones.L200, // Texto medio-claro

    // Error
    error = RiderFitColors.ErrorTones.L300,              // Rojo claro para visibilidad
    onError = RiderFitColors.ErrorTones.L900,            // Rojo muy oscuro
    errorContainer = RiderFitColors.ErrorTones.L800,     // Container oscuro
    onErrorContainer = RiderFitColors.ErrorTones.L100,   // Texto claro

    // Outline
    outline = RiderFitColors.NeutralTones.L400,          // Bordes claros
    outlineVariant = RiderFitColors.NeutralTones.L600,   // Bordes medio-oscuros

    // Additional Material 3 colors
    scrim = RiderFitColors.Scrim,                        // Negro transparente
    inverseSurface = RiderFitColors.NeutralTones.L100,   // Superficie inversa clara
    inverseOnSurface = RiderFitColors.NeutralTones.L800, // Texto sobre inversa
    inversePrimary = RiderFitColors.PrimaryTones.L700    // Primary invertido oscuro
)

/**
 * RiderFit Theme Composable
 * Aplica el tema Material 3 con los colores y tipografía de RiderFit
 *
 * @param darkTheme Si debe usar el tema oscuro (por defecto sigue el sistema)
 * @param content El contenido de la app a tematizar
 */
@Composable
fun RiderFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RiderFitTypography,
        content = content
    )
}

/**
 * Preview Theme para desarrollo
 * Siempre usa tema claro para consistencia en previews
 */
@Composable
fun RiderFitPreviewTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = RiderFitTypography,
        content = content
    )
}
