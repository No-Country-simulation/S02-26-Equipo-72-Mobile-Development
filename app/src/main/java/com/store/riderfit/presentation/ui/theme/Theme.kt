package com.store.riderfit.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    // Primary colors
    primary = RiderFitColors.Primary,
    onPrimary = RiderFitColors.White,
    primaryContainer = RiderFitColors.PrimaryLight,
    onPrimaryContainer = RiderFitColors.PrimaryDark,

    // Secondary colors
    secondary = RiderFitColors.Secondary,
    onSecondary = RiderFitColors.White,
    secondaryContainer = RiderFitColors.SecondaryLight,
    onSecondaryContainer = RiderFitColors.SecondaryDark,

    // Tertiary colors
    tertiary = RiderFitColors.Tertiary,
    onTertiary = RiderFitColors.White,
    tertiaryContainer = RiderFitColors.TertiaryLight,
    onTertiaryContainer = RiderFitColors.TertiaryDark,

    // Background & Surface
    background = RiderFitColors.LightBackground,
    onBackground = RiderFitColors.Gray900,
    surface = RiderFitColors.LightSurface,
    onSurface = RiderFitColors.Gray900,
    surfaceVariant = RiderFitColors.LightSurfaceVariant,
    onSurfaceVariant = RiderFitColors.Gray700,

    // Error
    error = RiderFitColors.Error,
    onError = RiderFitColors.White,
    errorContainer = Color(0xFFFEE7E6),
    onErrorContainer = Color(0xFF8B0000),

    // Outline
    outline = RiderFitColors.Gray600,
    outlineVariant = RiderFitColors.Gray400
)

private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    // Primary colors
    primary = RiderFitColors.PrimaryLight,
    onPrimary = RiderFitColors.PrimaryDark,
    primaryContainer = RiderFitColors.Primary,
    onPrimaryContainer = RiderFitColors.PrimaryLight,

    // Secondary colors
    secondary = RiderFitColors.SecondaryLight,
    onSecondary = RiderFitColors.SecondaryDark,
    secondaryContainer = RiderFitColors.Secondary,
    onSecondaryContainer = RiderFitColors.SecondaryLight,

    // Tertiary colors
    tertiary = RiderFitColors.TertiaryLight,
    onTertiary = RiderFitColors.TertiaryDark,
    tertiaryContainer = RiderFitColors.Tertiary,
    onTertiaryContainer = RiderFitColors.TertiaryLight,

    // Background & Surface
    background = RiderFitColors.DarkBackground,
    onBackground = RiderFitColors.Gray100,
    surface = RiderFitColors.DarkSurface,
    onSurface = RiderFitColors.Gray100,
    surfaceVariant = RiderFitColors.DarkSurfaceVariant,
    onSurfaceVariant = RiderFitColors.Gray400,

    // Error
    error = RiderFitColors.Error,
    onError = RiderFitColors.White,
    errorContainer = Color(0xFF5F0000),
    onErrorContainer = Color(0xFFFEE7E6),

    // Outline
    outline = RiderFitColors.Gray500,
    outlineVariant = RiderFitColors.Gray700
)

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
