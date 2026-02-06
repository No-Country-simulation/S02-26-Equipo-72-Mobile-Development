package com.store.riderfit.utils.validators

import com.store.riderfit.utils.Constants

/**
 * Validador de contraseñas reutilizable
 */
object PasswordValidator {

    /**
     * Niveles de fortaleza de contraseña
     */
    enum class PasswordStrength {
        WEAK,      // Muy débil
        FAIR,      // Débil
        GOOD,      // Normal
        STRONG,    // Fuerte
        VERY_STRONG // Muy fuerte
    }

    /**
     * Valida una contraseña y retorna el mensaje de error si es inválida
     * @param password La contraseña a validar
     * @return null si es válida, string con el error si no lo es
     */
    fun validate(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < Constants.Validation.MIN_PASSWORD_LENGTH ->
                "La contraseña debe tener al menos ${Constants.Validation.MIN_PASSWORD_LENGTH} caracteres"
            password.length > 128 ->
                "La contraseña no puede exceder 128 caracteres"
            else -> null
        }
    }

    /**
     * Verifica si una contraseña es válida
     * @param password La contraseña a validar
     * @return true si es válida, false si no
     */
    fun isValid(password: String): Boolean {
        return validate(password) == null
    }

    /**
     * Calcula la fortaleza de una contraseña
     * @param password La contraseña a evaluar
     * @return El nivel de fortaleza
     */
    fun getStrength(password: String): PasswordStrength {
        var score = 0

        // Longitud
        if (password.length >= Constants.Validation.MIN_PASSWORD_LENGTH) score++
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.length >= 16) score++

        // Mayúsculas
        if (password.contains(Regex("[A-Z]"))) score++

        // Minúsculas
        if (password.contains(Regex("[a-z]"))) score++

        // Números
        if (password.contains(Regex("[0-9]"))) score++

        // Caracteres especiales
        if (password.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/\\\\|`~]"))) score++

        return when {
            score <= 1 -> PasswordStrength.WEAK
            score <= 2 -> PasswordStrength.FAIR
            score <= 4 -> PasswordStrength.GOOD
            score <= 6 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }

    /**
     * Obtiene el color recomendado para mostrar la fortaleza de la contraseña
     * @param strength El nivel de fortaleza
     * @return Un string con el color (rojo, amarillo, verde)
     */
    fun getStrengthColor(strength: PasswordStrength): String {
        return when (strength) {
            PasswordStrength.WEAK -> "red"
            PasswordStrength.FAIR -> "orange"
            PasswordStrength.GOOD -> "yellow"
            PasswordStrength.STRONG -> "light_green"
            PasswordStrength.VERY_STRONG -> "green"
        }
    }

    /**
     * Obtiene el texto descriptivo de la fortaleza de la contraseña
     * @param strength El nivel de fortaleza
     * @return Descripción en español
     */
    fun getStrengthText(strength: PasswordStrength): String {
        return when (strength) {
            PasswordStrength.WEAK -> "Muy débil"
            PasswordStrength.FAIR -> "Débil"
            PasswordStrength.GOOD -> "Normal"
            PasswordStrength.STRONG -> "Fuerte"
            PasswordStrength.VERY_STRONG -> "Muy fuerte"
        }
    }

    /**
     * Obtiene recomendaciones para mejorar la fortaleza de la contraseña
     * @param password La contraseña a evaluar
     * @return Lista de recomendaciones
     */
    fun getRecommendations(password: String): List<String> {
        val recommendations = mutableListOf<String>()

        if (password.length < 12) {
            recommendations.add("Aumenta la longitud a al menos 12 caracteres")
        }

        if (!password.contains(Regex("[A-Z]"))) {
            recommendations.add("Agrega letras mayúsculas")
        }

        if (!password.contains(Regex("[a-z]"))) {
            recommendations.add("Agrega letras minúsculas")
        }

        if (!password.contains(Regex("[0-9]"))) {
            recommendations.add("Agrega números")
        }

        if (!password.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/\\\\|`~]"))) {
            recommendations.add("Agrega caracteres especiales (!@#$%^&*)")
        }

        return recommendations
    }

    /**
     * Verifica si dos contraseñas son iguales (case-sensitive)
     * @param password1 Primera contraseña
     * @param password2 Segunda contraseña
     * @return true si son iguales, false si no
     */
    fun matches(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    /**
     * Verifica si una contraseña contiene una secuencia común
     * @param password La contraseña a verificar
     * @return true si contiene secuencias comunes, false si no
     */
    fun hasCommonSequences(password: String): Boolean {
        val commonSequences = listOf(
            "123", "234", "345", "456", "567", "678", "789", "890",
            "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij",
            "qwerty", "asdf", "zxcv", "password", "admin", "letmein"
        )

        return commonSequences.any { password.lowercase().contains(it) }
    }

    /**
     * Obtiene un mensaje con consejos para crear una contraseña segura
     * @return String con consejos
     */
    fun getSecurityTips(): String {
        return """
            Tips para una contraseña segura:
            • Mínimo ${Constants.Validation.MIN_PASSWORD_LENGTH} caracteres
            • Mezcla mayúsculas y minúsculas
            • Incluye números (0-9)
            • Incluye caracteres especiales (!@#$%^&*)
            • Evita secuencias comunes (123, abc, qwerty)
            • No uses información personal (nombre, fecha nacimiento)
            • No reutilices contraseñas en otras cuentas
        """.trimIndent()
    }
}
