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
    }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_EMAIL_KEY] }

    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_TOKEN_KEY] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[USER_LOGGED_IN_KEY]?.toBoolean() ?: false }

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

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
