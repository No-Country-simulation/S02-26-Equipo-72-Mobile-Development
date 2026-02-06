package com.store.riderfit.utils

/**
 * Constantes globales de la aplicación RiderFit
 */
object Constants {

    // ==================== Información de la App ====================
    const val APP_NAME = "RiderFit"
    const val APP_VERSION = "1.0.0"

    // ==================== Firebase Firestore ====================
    object Firestore {
        const val USERS_COLLECTION = "users"
        const val PRODUCTS_COLLECTION = "products"
        const val ORDERS_COLLECTION = "orders"
        const val REVIEWS_COLLECTION = "reviews"
    }

    // ==================== Room Database ====================
    object Database {
        const val DB_NAME = "riderfit_database"
        const val DB_VERSION = 1
    }

    // ==================== DataStore ====================
    object DataStore {
        const val PREFERENCES_NAME = "riderfit_preferences"
        const val USER_ID_KEY = "user_id"
        const val USER_EMAIL_KEY = "user_email"
        const val IS_LOGGED_IN_KEY = "is_logged_in"
        const val AUTH_TOKEN_KEY = "auth_token"
        const val DARK_MODE_KEY = "dark_mode"
    }

    // ==================== Validaciones ====================
    object Validation {
        const val MIN_PASSWORD_LENGTH = 6
        const val MIN_DISPLAY_NAME_LENGTH = 2
        const val MAX_DISPLAY_NAME_LENGTH = 50
        const val MAX_EMAIL_LENGTH = 100
        const val MAX_BIO_LENGTH = 500
        
        // Regex patterns
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$"
        const val PHONE_PATTERN = "^[+]?[0-9]{8,}$"
        const val URL_PATTERN = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"
    }

    // ==================== Timeouts ====================
    object Timeout {
        const val SPLASH_DELAY_MS = 300L
        const val SPLASH_TIMEOUT_MS = 3000L
        const val NETWORK_TIMEOUT_SECONDS = 30L
        const val SYNC_INTERVAL_SECONDS = 60L
    }

    // ==================== UI Dimensions ====================
    object Dimensions {
        const val CORNER_RADIUS_SMALL = 4
        const val CORNER_RADIUS_MEDIUM = 8
        const val CORNER_RADIUS_LARGE = 16
        
        const val SPACING_XS = 4
        const val SPACING_SM = 8
        const val SPACING_MD = 12
        const val SPACING_LG = 16
        const val SPACING_XL = 20
        const val SPACING_XXL = 24
    }

    // ==================== Errores ====================
    object Errors {
        const val GENERIC_ERROR = "Ocurrió un error. Intenta nuevamente."
        const val NETWORK_ERROR = "Error de conexión. Verifica tu internet."
        const val AUTH_ERROR = "Error de autenticación."
        const val DATABASE_ERROR = "Error al acceder a la base de datos."
        const val NOT_FOUND = "Recurso no encontrado."
        const val UNAUTHORIZED = "No autorizado."
    }

    // ==================== Endpoints (Para futuro API) ====================
    object API {
        const val BASE_URL = "https://api.riderfit.com/"
        const val TIMEOUT_SECONDS = 30L
    }

    // ==================== Preferencias por defecto ====================
    object Defaults {
        const val DARK_MODE = false
        const val LANGUAGE = "es"
    }
}
