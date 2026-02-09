package com.store.riderfit.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * RiderFit Color Palette - System Design
 *
 * Tonal Palettes based on Material 3 Design System:
 * - Primary (Green): H: 79, S: 100
 * - Secondary (Beige/Yellow): H: 51, S: 100
 * - Neutral (Blue-Gray): H: 198, S: 20
 * - Error (Red): H: 359, S: 100
 */
object RiderFitColors {

    // ===== PRIMARY COLOR PALETTE (Green) =====
    // H: 79, S: 100, L: varies
    object PrimaryTones {
        val L900 = Color(0xFF1A2E00)    // Tono 900
        val L800 = Color(0xFF2D4600)    // Tono 800
        val L700 = Color(0xFF3F6600)    // Tono 700 - Principal
        val L600 = Color(0xFF518600)    // Tono 600
        val L500 = Color(0xFF63A600)    // Tono 500
        val L400 = Color(0xFF7BC600)    // Tono 400
        val L300 = Color(0xFF94E600)    // Tono 300
        val L200 = Color(0xFF9EFF00)    // Tono 200
        val L100 = Color(0xFFB8FF47)    // Tono 100
        val L50 = Color(0xFFD2FF87)     // Tono 50
        val L10 = Color(0xFFECFFD7)     // Tono 10
    }

    // Primary semantic colors for Material3
    val Primary = PrimaryTones.L700              // Principal
    val OnPrimary = Color(0xFFFFFFFF)            // Blanco sobre verde
    val PrimaryContainer = PrimaryTones.L50      // Container claro
    val OnPrimaryContainer = PrimaryTones.L900   // Texto oscuro sobre container

    // ===== SECONDARY COLOR PALETTE (Beige/Yellow) =====
    // H: 51, S: 100, L: varies
    object SecondaryTones {
        val L900 = Color(0xFF2E1F00)    // Tono 900
        val L800 = Color(0xFF463000)    // Tono 800
        val L700 = Color(0xFF664400)    // Tono 700 - Principal
        val L600 = Color(0xFF865A00)    // Tono 600
        val L500 = Color(0xFFA67000)    // Tono 500
        val L400 = Color(0xFFC68600)    // Tono 400
        val L300 = Color(0xFFE69C00)    // Tono 300
        val L200 = Color(0xFFFFB200)    // Tono 200
        val L100 = Color(0xFFFFCC47)    // Tono 100
        val L50 = Color(0xFFFFE687)     // Tono 50
        val L10 = Color(0xFFFFF8D7)     // Tono 10
    }

    // Secondary semantic colors for Material3
    val Secondary = SecondaryTones.L700          // Principal
    val OnSecondary = Color(0xFFFFFFFF)          // Blanco sobre beige
    val SecondaryContainer = SecondaryTones.L50  // Container claro
    val OnSecondaryContainer = SecondaryTones.L900 // Texto oscuro sobre container

    // ===== NEUTRAL COLOR PALETTE (Blue-Gray) =====
    // H: 198, S: 20, L: varies
    object NeutralTones {
        val L900 = Color(0xFF1A1C1E)    // Tono 900
        val L800 = Color(0xFF2E3133)    // Tono 800
        val L700 = Color(0xFF43474A)    // Tono 700
        val L600 = Color(0xFF595D61)    // Tono 600
        val L500 = Color(0xFF707478)    // Tono 500
        val L400 = Color(0xFF888B8F)    // Tono 400
        val L300 = Color(0xFFA0A3A7)    // Tono 300
        val L200 = Color(0xFFB9BCBF)    // Tono 200
        val L100 = Color(0xFFD1D4D8)    // Tono 100
        val L50 = Color(0xFFE8ECF0)     // Tono 50
        val L10 = Color(0xFFF5F9FD)     // Tono 10
    }

    // Neutral semantic colors
    val Background = Color(0xFFFFFBFF)           // Blanco puro para fondo
    val OnBackground = NeutralTones.L900         // Texto principal
    val Surface = Color(0xFFFFFBFF)              // Superficie blanca
    val OnSurface = NeutralTones.L900            // Texto sobre superficie
    val SurfaceVariant = NeutralTones.L50        // Variante de superficie
    val OnSurfaceVariant = NeutralTones.L600     // Texto sobre variante
    val Outline = NeutralTones.L300              // Bordes
    val OutlineVariant = NeutralTones.L100       // Bordes claros

    // Surface tones with primary overlay (Material 3)
    val SurfaceAt1 = Color(0xFFF8F9F6)      // +4% Primary
    val SurfaceAt2 = Color(0xFFF2F4EE)      // +8% Primary
    val SurfaceAt3 = Color(0xFFEDF0E6)      // +11% Primary
    val SurfaceAt4 = Color(0xFFE8EBDF)      // +12% Primary
    val SurfaceAt5 = Color(0xFFE3E6D7)      // +14% Primary

    // ===== ERROR COLOR PALETTE (Red) =====
    // H: 359, S: 100, L: varies
    object ErrorTones {
        val L900 = Color(0xFF410002)    // Tono 900
        val L800 = Color(0xFF5D1429)    // Tono 800
        val L700 = Color(0xFF7D1128)    // Tono 700
        val L600 = Color(0xFF9E0E27)    // Tono 600
        val L500 = Color(0xFFBA1A1A)    // Tono 500 - Principal
        val L400 = Color(0xFFDE3730)    // Tono 400
        val L300 = Color(0xFFFF5449)    // Tono 300
        val L200 = Color(0xFFFF897D)    // Tono 200
        val L100 = Color(0xFFFFB4AB)    // Tono 100
        val L50 = Color(0xFFFFDAD6)     // Tono 50
        val L10 = Color(0xFFFFFBFF)     // Tono 10
    }

    // Error semantic colors for Material3
    val Error = ErrorTones.L500                  // Principal
    val OnError = Color(0xFFFFFFFF)              // Blanco sobre error
    val ErrorContainer = ErrorTones.L50          // Container claro
    val OnErrorContainer = ErrorTones.L900       // Texto oscuro sobre container

    // ===== INVERSE COLORS =====
    val InverseSurface = NeutralTones.L800       // Superficie inversa
    val InverseOnSurface = NeutralTones.L50      // Texto sobre superficie inversa
    val InversePrimary = PrimaryTones.L200       // Primary invertido

    // ===== SCRIM =====
    val Scrim = Color(0x80000000)                // Negro transparente para overlays

    // ===== LEGACY ALIASES (for backward compatibility) =====
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Gray900 = NeutralTones.L900
    val Gray800 = NeutralTones.L800
    val Gray700 = NeutralTones.L700
    val Gray600 = NeutralTones.L600
    val Gray500 = NeutralTones.L500
    val Gray400 = NeutralTones.L400
    val Gray300 = NeutralTones.L300
    val Gray200 = NeutralTones.L200
    val Gray100 = NeutralTones.L100
    val Gray50 = NeutralTones.L50

    // Semantic aliases
    val Success = PrimaryTones.L600              // Verde para éxito
    val Warning = SecondaryTones.L500            // Amarillo/beige para advertencia
    val Info = NeutralTones.L500                 // Gris para información

    // ===== SPLASH SCREEN SPECIFIC COLORS =====
    val SplashOverlayGreen = PrimaryTones.L800   // Verde oscuro para overlay
    val SplashLogoWhite = Color(0xFFFAFFF0)      // Off-white para logo
}
