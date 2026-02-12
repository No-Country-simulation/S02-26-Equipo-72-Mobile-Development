package com.store.riderfit.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_LOGGED_IN_KEY = stringPreferencesKey("user_logged_in")
        private val ONBOARDING_COMPLETED_KEY = stringPreferencesKey("onboarding_completed")
        private val PERSONALIZATION_COMPLETED_KEY = stringPreferencesKey("personalization_completed")
        private val IS_GUEST_USER_KEY = stringPreferencesKey("is_guest_user")
    }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_EMAIL_KEY] }

    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_TOKEN_KEY] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[USER_LOGGED_IN_KEY]?.toBoolean() ?: false }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ONBOARDING_COMPLETED_KEY]?.toBoolean() ?: false }

    val hasCompletedPersonalization: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PERSONALIZATION_COMPLETED_KEY]?.toBoolean() ?: false }

    val isGuestUser: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_GUEST_USER_KEY]?.toBoolean() ?: false }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    suspend fun saveUserToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USER_LOGGED_IN_KEY] = isLoggedIn.toString()
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed.toString()
        }
    }

    suspend fun setPersonalizationCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PERSONALIZATION_COMPLETED_KEY] = completed.toString()
        }
    }

    suspend fun setGuestUser(isGuest: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GUEST_USER_KEY] = isGuest.toString()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Función para debugging - limpia solo las preferencias de onboarding y guest
     * SOLO PARA DESARROLLO - NO USAR EN PRODUCCIÓN
     */
    suspend fun clearOnboardingAndGuestForDebug() {
        context.dataStore.edit { preferences ->
            preferences.remove(ONBOARDING_COMPLETED_KEY)
            preferences.remove(IS_GUEST_USER_KEY)
        }
        android.util.Log.d("UserPreferences", "DEBUG: Limpiadas preferencias de onboarding y guest")
    }

    /**
     * Función para debugging - mostrar todas las preferencias
     * SOLO PARA DESARROLLO
     */
    suspend fun debugShowAllPreferences() {
        context.dataStore.data.collect { preferences ->
            android.util.Log.d("UserPreferences", "=== DEBUG PREFERENCIAS ===")
            android.util.Log.d("UserPreferences", "USER_ID_KEY: ${preferences[USER_ID_KEY]}")
            android.util.Log.d("UserPreferences", "USER_EMAIL_KEY: ${preferences[USER_EMAIL_KEY]}")
            android.util.Log.d("UserPreferences", "USER_TOKEN_KEY: ${preferences[USER_TOKEN_KEY]}")
            android.util.Log.d("UserPreferences", "USER_LOGGED_IN_KEY: ${preferences[USER_LOGGED_IN_KEY]}")
            android.util.Log.d("UserPreferences", "ONBOARDING_COMPLETED_KEY: ${preferences[ONBOARDING_COMPLETED_KEY]}")
            android.util.Log.d(
                "UserPreferences",
                "PERSONALIZATION_COMPLETED_KEY: ${preferences[PERSONALIZATION_COMPLETED_KEY]}"
            )
            android.util.Log.d("UserPreferences", "IS_GUEST_USER_KEY: ${preferences[IS_GUEST_USER_KEY]}")
            android.util.Log.d("UserPreferences", "========================")
        }
    }
}
