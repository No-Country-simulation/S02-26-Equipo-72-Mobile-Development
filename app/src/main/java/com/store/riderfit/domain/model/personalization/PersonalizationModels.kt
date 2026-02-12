package com.store.riderfit.domain.model.personalization

/**
 * Disciplinas ecuestres disponibles
 */
enum class EquestrianDiscipline(val displayName: String) {
    JUMPING("Salto"),
    CLASSICAL_DRESSAGE("Doma clásica"),
    ENDURANCE("Endurance"),
    RECREATIONAL_DAILY_WORK("Recreativo / trabajo diario"),
    OTHER("Otra");

    companion object {
        fun fromDisplayName(displayName: String): EquestrianDiscipline? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Tipos de sangre del caballo
 */
enum class BloodType(val displayName: String) {
    PURE_ENGLISH("Pura sangre inglés"),
    PURE_ARABIAN("Pura sangre árabe"),
    QUARTER_HORSE("Cuarto de milla"),
    WARMBLOOD("Sangre caliente"),
    OTHER("Otro");

    companion object {
        fun fromDisplayName(displayName: String): BloodType? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Conformación física del caballo
 */
enum class HorseConformation(val displayName: String) {
    SHORT("Corto"),
    LONG("Largo");

    companion object {
        fun fromDisplayName(displayName: String): HorseConformation? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Nivel de sensibilidad del caballo
 */
enum class SensitivityLevel(val displayName: String) {
    YES("Sí"),
    NO("No"),
    NOT_SURE("No estoy seguro");

    companion object {
        fun fromDisplayName(displayName: String): SensitivityLevel? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Nivel de experiencia del jinete
 */
enum class RiderLevel(val displayName: String, val description: String) {
    BEGINNER("Principiante", "Define el nivel de soporte recomendado"),
    AMATEUR("Aficionado", "Nivel recreativo"),
    INTERMEDIATE("Intermedio", "Experiencia moderada"),
    ADVANCED("Avanzado", "Alto nivel de experiencia");

    companion object {
        fun fromDisplayName(displayName: String): RiderLevel? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Preferencias de equipamiento
 */
enum class EquipmentPreference(val displayName: String) {
    MORE_SUPPORT("Más soporte"),
    EQUILIBRIUM("Equilibrado"),
    MORE_FREEDOM("Más libertad de movimiento");

    companion object {
        fun fromDisplayName(displayName: String): EquipmentPreference? {
            return values().find { it.displayName == displayName }
        }
    }
}

/**
 * Paso 1: Información de disciplina
 */
data class DisciplineInfo(
    val discipline: EquestrianDiscipline,
    val customDiscipline: String? = null // Para "Otra"
) {
    fun isValid(): Boolean {
        return when (discipline) {
            EquestrianDiscipline.OTHER -> !customDiscipline.isNullOrBlank()
            else -> true
        }
    }
}

/**
 * Paso 2: Información del caballo
 */
data class HorseInfo(
    val name: String,
    val bloodType: BloodType,
    val age: Int?, // En años
    val height: Float?, // En metros (ej: 1.68m)
    val conformation: HorseConformation,
    val sensitivity: SensitivityLevel
) {
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                age != null && age > 0 &&
                height != null && height > 0.5f && height < 3.0f
    }

    fun getHeightInCm(): Int? {
        return height?.let { (it * 100).toInt() }
    }
}

/**
 * Paso 3: Información del jinete
 */
data class RiderInfo(
    val level: RiderLevel,
    val height: Float?, // En metros
    val weight: Float?, // En kg
    val bootSize: String?, // Talla de bota (ej: "38 L/M")
    val helmetSize: String?, // Talla de casco (ej: "58")
    val preferences: Set<EquipmentPreference>
) {
    fun isValid(): Boolean {
        return height != null && height > 1.0f && height < 2.5f &&
                weight != null && weight > 20f && weight < 200f &&
                !bootSize.isNullOrBlank() &&
                !helmetSize.isNullOrBlank()
    }

    fun getHeightInCm(): Int? {
        return height?.let { (it * 100).toInt() }
    }
}

/**
 * Perfil completo del usuario
 */
data class UserPersonalizationProfile(
    val userId: String,
    val disciplineInfo: DisciplineInfo,
    val horseInfo: HorseInfo,
    val riderInfo: RiderInfo,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isComplete(): Boolean {
        return disciplineInfo.isValid() &&
                horseInfo.isValid() &&
                riderInfo.isValid()
    }

    fun getCompletionPercentage(): Float {
        var completed = 0
        var total = 3

        if (disciplineInfo.isValid()) completed++
        if (horseInfo.isValid()) completed++
        if (riderInfo.isValid()) completed++

        return completed.toFloat() / total.toFloat()
    }
}

/**
 * Estado de progreso de personalización
 */
data class PersonalizationProgress(
    val currentStep: Int = 1,
    val totalSteps: Int = 3,
    val disciplineInfo: DisciplineInfo? = null,
    val horseInfo: HorseInfo? = null,
    val riderInfo: RiderInfo? = null
) {
    fun getProgressPercentage(): Float {
        return when (currentStep) {
            1 -> 0.25f
            2 -> 0.75f
            3 -> 1.0f
            else -> 0f
        }
    }

    fun isStepComplete(step: Int): Boolean {
        return when (step) {
            1 -> disciplineInfo?.isValid() == true
            2 -> horseInfo?.isValid() == true
            3 -> riderInfo?.isValid() == true
            else -> false
        }
    }

    fun canProceedToNext(): Boolean {
        return isStepComplete(currentStep)
    }

    fun toUserProfile(userId: String): UserPersonalizationProfile? {
        return if (disciplineInfo != null && horseInfo != null && riderInfo != null) {
            UserPersonalizationProfile(
                userId = userId,
                disciplineInfo = disciplineInfo,
                horseInfo = horseInfo,
                riderInfo = riderInfo
            )
        } else {
            null
        }
    }
}
