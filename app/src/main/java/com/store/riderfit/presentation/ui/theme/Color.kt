package com.store.riderfit.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * RiderFit Color Palette - Light Theme
 * 
 * Tonal Palettes based on:
 * - Primary (Green): H: 79, S: 100
 * - Secondary (Orange/Brown): H: 51, S: 100
 * - Neutral (Blue-Gray): H: 198, S: 20
 * - Error (Red): H: 359, S: 100
 */
object RiderFitColors {
    
    // ===== PRIMARY COLOR PALETTE (Green) =====
    // H: 79, S: 100, L: varies
    object PrimaryTones {
        val L900 = Color(0xFF0D1F00)    // L: 7
        val L800 = Color(0xFF132600)    // L: 10
        val L700 = Color(0xFF1F4600)    // L: 17
        val L600 = Color(0xFF336600)    // L: 27
        val L500 = Color(0xFF4D8600)    // L: 37
        val L400 = Color(0xFF66A500)    // L: 47
        val L300 = Color(0xFF80BF00)    // L: 57
        val L200 = Color(0xFF99D900)    // L: 67
        val L100 = Color(0xFFB3FF00)    // L: 77
        val L50 = Color(0xFFCCFF66)     // L: 87
        val L10 = Color(0xFFF0FFF0)     // L: 97
    }
    
    // Primary semantic colors for Material3
    val Primary = PrimaryTones.L700              // Principal
    val OnPrimary = Color(0xFFF0FFF0)            // 10
    val PrimaryContainer = PrimaryTones.L50      // 50
    val OnPrimaryContainer = PrimaryTones.L900   // 900
    
    // ===== SECONDARY COLOR PALETTE (Orange/Brown) =====
    // H: 51, S: 100, L: varies
    object SecondaryTones {
        val L900 = Color(0xFF331F00)    // L: 7
        val L800 = Color(0xFF4D2600)    // L: 10
        val L700 = Color(0xFF664D00)    // L: 17
        val L600 = Color(0xFF996600)    // L: 27
        val L500 = Color(0xFFCC8800)    // L: 37
        val L400 = Color(0xFFFFAA00)    // L: 47
        val L300 = Color(0xFFFFBB33)    // L: 57
        val L200 = Color(0xFFFFCC66)    // L: 67
        val L100 = Color(0xFFFFDD99)    // L: 77
        val L50 = Color(0xFFFFEE99)     // L: 87
        val L10 = Color(0xFFFFFBF0)     // L: 97
    }
    
    // Secondary semantic colors for Material3
    val Secondary = SecondaryTones.L700          // Principal
    val OnSecondary = SecondaryTones.L10         // 10
    val SecondaryContainer = SecondaryTones.L50  // 50
    val OnSecondaryContainer = SecondaryTones.L900 // 900
    
    // ===== NEUTRAL COLOR PALETTE (Blue-Gray) =====
    // H: 198, S: 20, L: varies
    object NeutralTones {
        val L900 = Color(0xFF030303)    // L: 1
        val L800 = Color(0xFFE3E8EB)    // L: 89
        val L700 = Color(0xFF1D2E35)    // L: 17
        val L600 = Color(0xFF334856)    // L: 25
        val L500 = Color(0xFF496378)    // L: 33
        val L400 = Color(0xFF5F7C99)    // L: 41
        val L300 = Color(0xFF7595BA)    // L: 49
        val L200 = Color(0xFF8AADDA)    // L: 57
        val L100 = Color(0xFFA0C5EB)    // L: 65
        val L50 = Color(0xFFB5DDFB)     // L: 73
        val L10 = Color(0xFFF0F5F9)     // L: 97
    }
    
    // Neutral semantic colors
    val Background = NeutralTones.L10            // 10
    val OnBackground = NeutralTones.L700         // 700
    val Surface = NeutralTones.L10               // 10
    val OnSurface = NeutralTones.L900            // 900
    val SurfaceVariant = NeutralTones.L700       // 700
    val OnSurfaceVariant = NeutralTones.L600     // 600
    
    // Surface tones with primary overlay
    val SurfaceAt1 = Color(0xFFF5F6F3)      // +4% Primary
    val SurfaceAt2 = Color(0xFFF0F2EB)      // +8% Primary
    val SurfaceAt3 = Color(0xFFEBEDE3)      // +10% Primary
    val SurfaceAt4 = Color(0xFFE6E9DB)      // +12% Primary
    val SurfaceAt5 = Color(0xFFE1E4D3)      // +14% Primary
    
    // ===== ERROR COLOR PALETTE (Red) =====
    // H: 359, S: 100, L: varies
    object ErrorTones {
        val L900 = Color(0xFF661F1F)    // L: 24
        val L800 = Color(0xFF993333)    // L: 32
        val L700 = Color(0xFFCC4444)    // L: 40
        val L600 = Color(0xFFFF5555)    // L: 48
        val L500 = Color(0xFFFF8888)    // L: 56
        val L400 = Color(0xFFFFBBBB)    // L: 64
        val L300 = Color(0xFFFFDDDD)    // L: 72
        val L200 = Color(0xFFFFEEEE)    // L: 80
        val L100 = Color(0xFFFFF5F5)    // L: 88
        val L50 = Color(0xFFFFFAFA)     // L: 96
        val L10 = Color(0xFFFFFBFB)     // L: 99
    }
    
    // Error semantic colors for Material3
    val Error = ErrorTones.L700                  // Principal
    val OnError = ErrorTones.L10                 // 10
    val ErrorContainer = ErrorTones.L50          // 50
    val OnErrorContainer = ErrorTones.L900       // 900
    
    // ===== SPLASH SCREEN SPECIFIC COLORS =====
    val SplashOverlayGreen = Color(0xFF3A5500)  // Original green overlay
    val SplashLogoWhite = Color(0xFFFAFFF0)     // Off-white for logo
    
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
    val Success = PrimaryTones.L600
    val Warning = SecondaryTones.L500
    val Info = NeutralTones.L400
}
