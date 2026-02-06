package com.store.riderfit.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extensiones útiles para String
 */

/**
 * Valida si el email tiene formato correcto
 */
fun String.isValidEmail(): Boolean {
    return this.matches(Regex(Constants.Validation.EMAIL_PATTERN))
}

/**
 * Valida si la contraseña cumple con requisitos mínimos
 */
fun String.isValidPassword(): Boolean {
    return this.length >= Constants.Validation.MIN_PASSWORD_LENGTH
}

/**
 * Valida si el nombre tiene longitud válida
 */
fun String.isValidDisplayName(): Boolean {
    return this.length >= Constants.Validation.MIN_DISPLAY_NAME_LENGTH &&
            this.length <= Constants.Validation.MAX_DISPLAY_NAME_LENGTH
}

/**
 * Valida si es un teléfono válido
 */
fun String.isValidPhone(): Boolean {
    return this.matches(Regex(Constants.Validation.PHONE_PATTERN))
}

/**
 * Verifica si es una URL válida
 */
fun String.isValidUrl(): Boolean {
    return this.matches(Regex(Constants.Validation.URL_PATTERN))
}

/**
 * Valida si el email no está vacío y tiene formato válido
 */
fun String.isValidEmailOrEmpty(): Boolean {
    return this.isEmpty() || this.isValidEmail()
}

/**
 * Trunca el string a una longitud máxima
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength) + "..."
    } else {
        this
    }
}

/**
 * Capitaliza la primera letra
 */
fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1)
    } else {
        this
    }
}

/**
 * Elimina espacios en blanco al inicio y final
 */
fun String.trimWhitespace(): String {
    return this.trim()
}

/**
 * Extensiones útiles para Boolean
 */

/**
 * Retorna una cadena "Sí" o "No" según el valor booleano
 */
fun Boolean.toYesNo(): String {
    return if (this) "Sí" else "No"
}

/**
 * Extensiones útiles para Long (timestamps)
 */

/**
 * Convierte un timestamp a formato de fecha legible
 */
fun Long.toFormattedDate(format: String = "dd/MM/yyyy"): String {
    return try {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        formatter.format(Date(this))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

/**
 * Convierte un timestamp a formato de fecha y hora legible
 */
fun Long.toFormattedDateTime(format: String = "dd/MM/yyyy HH:mm"): String {
    return try {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        formatter.format(Date(this))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

/**
 * Calcula cuánto tiempo ha pasado desde el timestamp
 */
fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diffMs = now - this
    
    return when {
        diffMs < 60_000 -> "Hace un momento"
        diffMs < 3_600_000 -> "Hace ${diffMs / 60_000} minutos"
        diffMs < 86_400_000 -> "Hace ${diffMs / 3_600_000} horas"
        diffMs < 604_800_000 -> "Hace ${diffMs / 86_400_000} días"
        diffMs < 2_592_000_000 -> "Hace ${diffMs / 604_800_000} semanas"
        else -> "Hace ${diffMs / 2_592_000_000} meses"
    }
}

/**
 * Extensiones útiles para Double (precios, números)
 */

/**
 * Formatea un Double como precio con 2 decimales
 */
fun Double.formatPrice(): String {
    return "$${"%.2f".format(this)}"
}

/**
 * Formatea un Double como porcentaje
 */
fun Double.formatPercentage(): String {
    return "${"%.1f".format(this)}%"
}

/**
 * Redondea a N decimales
 */
fun Double.roundTo(decimals: Int): Double {
    val multiplier = Math.pow(10.0, decimals.toDouble())
    return Math.round(this * multiplier) / multiplier
}

/**
 * Extensiones útiles para Collections
 */

/**
 * Convierte una lista en una cadena separada por comas
 */
fun <T> List<T>.joinToString(separator: String = ", "): String {
    return this.joinToString(separator)
}

/**
 * Verifica si la lista no está vacía de forma segura
 */
fun <T> List<T>?.isNotEmptyOrNull(): Boolean {
    return this != null && this.isNotEmpty()
}

/**
 * Obtiene un elemento de forma segura o null
 */
fun <T> List<T>?.getOrNull(index: Int): T? {
    return if (index >= 0 && index < (this?.size ?: 0)) {
        this?.get(index)
    } else {
        null
    }
}

/**
 * Extensiones útiles para Int
 */

/**
 * Convierte píxeles en DP
 */
fun Int.pxToDp(density: Float): Int {
    return (this / density).toInt()
}

/**
 * Convierte DP a píxeles
 */
fun Int.dpToPx(density: Float): Int {
    return (this * density).toInt()
}
