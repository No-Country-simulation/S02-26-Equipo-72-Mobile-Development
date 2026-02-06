package com.store.riderfit.utils.validators

import com.store.riderfit.utils.Constants

/**
 * Validador de emails reutilizable
 */
object EmailValidator {

    /**
     * Valida un email y retorna el mensaje de error si es inválido
     * @param email El email a validar
     * @return null si es válido, string con el error si no lo es
     */
    fun validate(email: String): String? {
        return when {
            email.isBlank() -> "El email es requerido"
            email.length > Constants.Validation.MAX_EMAIL_LENGTH -> 
                "El email no puede exceder ${Constants.Validation.MAX_EMAIL_LENGTH} caracteres"
            !email.matches(Regex(Constants.Validation.EMAIL_PATTERN)) ->
                "El formato del email no es válido"
            else -> null
        }
    }

    /**
     * Verifica si un email es válido
     * @param email El email a validar
     * @return true si es válido, false si no
     */
    fun isValid(email: String): Boolean {
        return validate(email) == null
    }

    /**
     * Obtiene el dominio de un email
     * @param email El email del cual extraer el dominio
     * @return El dominio (ej: "gmail.com") o null si es inválido
     */
    fun getDomain(email: String): String? {
        return try {
            email.substringAfter("@").takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Normaliza un email (convierte a minúsculas y elimina espacios)
     * @param email El email a normalizar
     * @return El email normalizado
     */
    fun normalize(email: String): String {
        return email.trim().lowercase()
    }

    /**
     * Verifica si dos emails son iguales (case-insensitive)
     * @param email1 Primer email
     * @param email2 Segundo email
     * @return true si son iguales, false si no
     */
    fun equals(email1: String, email2: String): Boolean {
        return normalize(email1) == normalize(email2)
    }

    /**
     * Obtiene el nombre de usuario del email (parte antes del @)
     * @param email El email del cual extraer el nombre de usuario
     * @return El nombre de usuario o null si es inválido
     */
    fun getUsername(email: String): String? {
        return try {
            email.substringBefore("@").takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica si un email es de un dominio específico
     * @param email El email a verificar
     * @param domain El dominio a buscar (ej: "gmail.com")
     * @return true si el email es del dominio especificado, false si no
     */
    fun isFromDomain(email: String, domain: String): Boolean {
        return getDomain(email.lowercase())?.equals(domain.lowercase()) == true
    }

    /**
     * Obtiene una lista de sugerencias para un email inválido
     * @param email El email ingresado por el usuario
     * @return Lista de sugerencias de emails válidos
     */
    fun getSuggestions(email: String): List<String> {
        val suggestions = mutableListOf<String>()
        val normalizedEmail = normalize(email)
        
        // Sugerencias comunes de dominios si falta el @
        if (!normalizedEmail.contains("@")) {
            val commonDomains = listOf("gmail.com", "outlook.com", "yahoo.com", "hotmail.com")
            commonDomains.forEach { domain ->
                suggestions.add("$normalizedEmail@$domain")
            }
        }
        
        return suggestions.filter { isValid(it) }
    }
}
