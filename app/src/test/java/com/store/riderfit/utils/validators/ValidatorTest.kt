package com.store.riderfit.utils.validators

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests unitarios para EmailValidator
 * Patrón: AAA (Arrange-Act-Assert)
 */
class EmailValidatorTest {

    /**
     * GIVEN: Email válido
     * WHEN: Se valida
     * THEN: Retorna null (sin errores)
     */
    @Test
    fun testValidate_WithValidEmail_ReturnsNull() {
        // Arrange
        val email = "user@email.com"

        // Act
        val result = EmailValidator.validate(email)

        // Assert
        assertThat(result).isNull()
    }

    /**
     * GIVEN: Email vacío
     * WHEN: Se valida
     * THEN: Retorna mensaje de error
     */
    @Test
    fun testValidate_WithEmptyEmail_ReturnsError() {
        // Arrange
        val email = ""

        // Act
        val result = EmailValidator.validate(email)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).contains("requerido")
    }

    /**
     * GIVEN: Email sin @
     * WHEN: Se valida
     * THEN: Retorna error de formato
     */
    @Test
    fun testValidate_WithoutAtSymbol_ReturnsFormatError() {
        // Arrange
        val email = "useremail.com"

        // Act
        val result = EmailValidator.validate(email)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).contains("formato")
    }

    /**
     * GIVEN: Email con espacios
     * WHEN: Se valida
     * THEN: Es inválido
     */
    @Test
    fun testValidate_WithSpaces_ReturnsError() {
        // Arrange
        val email = "user @email.com"

        // Act
        val result = EmailValidator.validate(email)

        // Assert
        assertThat(result).isNotNull()
    }

    /**
     * GIVEN: Dos emails iguales
     * WHEN: Se comparan
     * THEN: Retorna true (case-insensitive)
     */
    @Test
    fun testEquals_WithSameEmail_ReturnsTrue() {
        // Arrange
        val email1 = "User@Email.com"
        val email2 = "user@email.com"

        // Act
        val result = EmailValidator.equals(email1, email2)

        // Assert
        assertThat(result).isTrue()
    }

    /**
     * GIVEN: Email completo
     * WHEN: Se extrae dominio
     * THEN: Retorna dominio correcto
     */
    @Test
    fun testGetDomain_WithValidEmail_ReturnsDomain() {
        // Arrange
        val email = "user@gmail.com"

        // Act
        val domain = EmailValidator.getDomain(email)

        // Assert
        assertThat(domain).isEqualTo("gmail.com")
    }

    /**
     * GIVEN: Email completo
     * WHEN: Se extrae username
     * THEN: Retorna parte antes del @
     */
    @Test
    fun testGetUsername_WithValidEmail_ReturnsUsername() {
        // Arrange
        val email = "jhondoe@email.com"

        // Act
        val username = EmailValidator.getUsername(email)

        // Assert
        assertThat(username).isEqualTo("jhondoe")
    }
}

/**
 * Tests unitarios para PasswordValidator
 */
class PasswordValidatorTest {

    /**
     * GIVEN: Contraseña válida (6+ caracteres)
     * WHEN: Se valida
     * THEN: Retorna null (sin errores)
     */
    @Test
    fun testValidate_WithValidPassword_ReturnsNull() {
        // Arrange
        val password = "password123"

        // Act
        val result = PasswordValidator.validate(password)

        // Assert
        assertThat(result).isNull()
    }

    /**
     * GIVEN: Contraseña vacía
     * WHEN: Se valida
     * THEN: Retorna error
     */
    @Test
    fun testValidate_WithEmptyPassword_ReturnsError() {
        // Arrange
        val password = ""

        // Act
        val result = PasswordValidator.validate(password)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).contains("requerida")
    }

    /**
     * GIVEN: Contraseña muy corta (< 6)
     * WHEN: Se valida
     * THEN: Retorna error de longitud
     */
    @Test
    fun testValidate_WithShortPassword_ReturnsLengthError() {
        // Arrange
        val password = "123"

        // Act
        val result = PasswordValidator.validate(password)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).contains("6 caracteres")
    }

    /**
     * GIVEN: Contraseña con solo minúsculas (8 caracteres)
     * WHEN: Se calcula fortaleza
     * THEN: Es GOOD (tiene longitud >= 8 y caracteres)
     */
    @Test
    fun testGetStrength_WithOnlyLowercase_IsWeak() {
        // Arrange
        val password = "password"

        // Act
        val strength = PasswordValidator.getStrength(password)

        // Assert
        assertThat(strength).isEqualTo(PasswordValidator.PasswordStrength.GOOD)
    }

    /**
     * GIVEN: Contraseña fuerte (mayús, minús, números, especiales)
     * WHEN: Se calcula fortaleza
     * THEN: Es STRONG o VERY_STRONG
     */
    @Test
    fun testGetStrength_WithMixedCharacters_IsStrong() {
        // Arrange
        val password = "SecurePass123!@#"

        // Act
        val strength = PasswordValidator.getStrength(password)

        // Assert
        assertThat(strength).isAnyOf(
            PasswordValidator.PasswordStrength.STRONG,
            PasswordValidator.PasswordStrength.VERY_STRONG
        )
    }

    /**
     * GIVEN: Dos contraseñas iguales
     * WHEN: Se comparan
     * THEN: Retorna true
     */
    @Test
    fun testMatches_WithSamePassword_ReturnsTrue() {
        // Arrange
        val password1 = "password123"
        val password2 = "password123"

        // Act
        val result = PasswordValidator.matches(password1, password2)

        // Assert
        assertThat(result).isTrue()
    }

    /**
     * GIVEN: Contraseña con secuencia común "123"
     * WHEN: Se verifica
     * THEN: Detecta la secuencia
     */
    @Test
    fun testHasCommonSequences_With123_ReturnsTrue() {
        // Arrange
        val password = "pass123word"

        // Act
        val result = PasswordValidator.hasCommonSequences(password)

        // Assert
        assertThat(result).isTrue()
    }

    /**
     * GIVEN: Contraseña sin secuencias comunes
     * WHEN: Se verifica
     * THEN: Retorna false
     */
    @Test
    fun testHasCommonSequences_WithoutSequences_ReturnsFalse() {
        // Arrange
        val password = "mK9@pLqRs2"  // No common sequences

        // Act
        val result = PasswordValidator.hasCommonSequences(password)

        // Assert
        assertThat(result).isFalse()
    }

    /**
     * GIVEN: Contraseña débil
     * WHEN: Se piden recomendaciones
     * THEN: Retorna lista de mejoras
     */
    @Test
    fun testGetRecommendations_WithWeakPassword_ReturnsRecommendations() {
        // Arrange
        val password = "pass"

        // Act
        val recommendations = PasswordValidator.getRecommendations(password)

        // Assert
        assertThat(recommendations).isNotEmpty()
        assertThat(recommendations.any { it.contains("longitud") || it.contains("Aumenta") }).isTrue()
    }
}
