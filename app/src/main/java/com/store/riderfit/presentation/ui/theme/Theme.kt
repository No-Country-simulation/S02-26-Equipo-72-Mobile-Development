package com.store.riderfit.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Light Theme Color Scheme for RiderFit
 * Based on tonal palettes with:
 * - Primary: Green (H: 79, S: 100)
 * - Secondary: Orange/Brown (H: 51, S: 100)
 * - Neutral: Blue-Gray (H: 198, S: 20)
 * - Error: Red (H: 359, S: 100)
 */
private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    // Primary colors
    primary = RiderFitColors.Primary,                    // L700 - Principal green
    onPrimary = RiderFitColors.OnPrimary,                // L10 - Light green
    primaryContainer = RiderFitColors.PrimaryContainer,  // L50 - Very light green
    onPrimaryContainer = RiderFitColors.OnPrimaryContainer, // L900 - Very dark green

    // Secondary colors
    secondary = RiderFitColors.Secondary,                // L700 - Principal orange
    onSecondary = RiderFitColors.OnSecondary,            // L10 - Light orange
    secondaryContainer = RiderFitColors.SecondaryContainer, // L50 - Very light orange
    onSecondaryContainer = RiderFitColors.OnSecondaryContainer, // L900 - Very dark orange

    // Tertiary (using from Secondary palette for light theme)
    tertiary = RiderFitColors.SecondaryTones.L400,            // Orange for tertiary
    onTertiary = Color.White,
    tertiaryContainer = RiderFitColors.SecondaryTones.L100,   // Light orange
    onTertiaryContainer = RiderFitColors.SecondaryTones.L900, // Dark orange

    // Background & Surface (from Neutral palette)
    background = RiderFitColors.Background,              // L10 - Very light gray
    onBackground = RiderFitColors.OnBackground,          // L700 - Dark gray for text
    surface = RiderFitColors.Surface,                    // L10 - Surface white
    onSurface = RiderFitColors.OnSurface,                // L900 - Very dark text
    surfaceVariant = RiderFitColors.SurfaceVariant,      // L700 - Gray variant
    onSurfaceVariant = RiderFitColors.OnSurfaceVariant,  // L600 - Medium gray for secondary text

    // Error
    error = RiderFitColors.Error,                        // L700 - Principal red
    onError = RiderFitColors.OnError,                    // L10 - Light for text on error
    errorContainer = RiderFitColors.ErrorContainer,      // L50 - Very light red
    onErrorContainer = RiderFitColors.OnErrorContainer,  // L900 - Very dark red

    // Outline
    outline = RiderFitColors.NeutralTones.L600,               // Medium gray for outlines
    outlineVariant = RiderFitColors.NeutralTones.L400,        // Light gray for variant outlines
    
    // Additional
    scrim = Color.Black                                  // For scrim overlays
)

/**
 * Dark Theme Color Scheme for RiderFit (if needed in future)
 */
private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    // Primary colors - inverted for dark mode
    primary = RiderFitColors.PrimaryTones.L300,               // Lighter green for visibility
    onPrimary = RiderFitColors.PrimaryTones.L900,             // Dark text on light primary
    primaryContainer = RiderFitColors.PrimaryTones.L700,      // Container in dark
    onPrimaryContainer = RiderFitColors.PrimaryTones.L100,    // Light text in container

    // Secondary colors
    secondary = RiderFitColors.SecondaryTones.L300,           // Lighter orange
    onSecondary = RiderFitColors.SecondaryTones.L900,         // Dark text on orange
    secondaryContainer = RiderFitColors.SecondaryTones.L700,  // Container in dark
    onSecondaryContainer = RiderFitColors.SecondaryTones.L100, // Light text in container

    // Tertiary
    tertiary = RiderFitColors.SecondaryTones.L200,            // Very light orange
    onTertiary = RiderFitColors.SecondaryTones.L900,          // Dark text
    tertiaryContainer = RiderFitColors.SecondaryTones.L600,   // Container
    onTertiaryContainer = RiderFitColors.SecondaryTones.L50,  // Light text

    // Background & Surface - dark variants
    background = RiderFitColors.NeutralTones.L900,            // Very dark background
    onBackground = RiderFitColors.NeutralTones.L50,           // Very light text
    surface = RiderFitColors.NeutralTones.L800,               // Dark surface (actually light in HSL)
    onSurface = RiderFitColors.NeutralTones.L10,              // Light text on surface
    surfaceVariant = RiderFitColors.NeutralTones.L700,        // Gray variant
    onSurfaceVariant = RiderFitColors.NeutralTones.L200,      // Medium light text

    // Error
    error = RiderFitColors.ErrorTones.L400,                   // Lighter red for visibility
    onError = RiderFitColors.ErrorTones.L900,                 // Dark text on red
    errorContainer = RiderFitColors.ErrorTones.L700,          // Red container
    onErrorContainer = RiderFitColors.ErrorTones.L50,         // Light text in container

    // Outline
    outline = RiderFitColors.NeutralTones.L400,               // Light gray for outlines
    outlineVariant = RiderFitColors.NeutralTones.L600,        // Darker gray variant
    
    // Additional
    scrim = Color.Black                                  // For scrim overlays
)

/**
 * RiderFit Theme Composable
 * Applies Material3 theme with RiderFit color scheme and typography
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
