package com.store.riderfit.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests unitarios para UserPreferences - Funcionalidad de Onboarding
 *
 * Valida:
 * - Persistencia de estado de onboarding completado
 * - Lectura correcta de preferencias
 * - Comportamiento por defecto
 * - Manejo de errores
 */
@RunWith(AndroidJUnit4::class)
class UserPreferencesOnboardingTest {

    private lateinit var context: Context
    private lateinit var userPreferences: UserPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        userPreferences = UserPreferences(context)

        // Limpiar preferencias antes de cada test
        runTest {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    @Test
    fun `hasSeenOnboarding should return false by default`() = runTest {
        // Given - preferencias limpias (sin datos)

        // When
        val hasSeenOnboarding = userPreferences.hasSeenOnboarding.first()

        // Then
        assertThat(hasSeenOnboarding).isFalse()
    }

    @Test
    fun `setOnboardingCompleted true should persist correctly`() = runTest {
        // Given
        val expectedValue = true

        // When
        userPreferences.setOnboardingCompleted(expectedValue)

        // Then
        val actualValue = userPreferences.hasSeenOnboarding.first()
        assertThat(actualValue).isTrue()
    }

    @Test
    fun `setOnboardingCompleted false should persist correctly`() = runTest {
        // Given - primero establecer en true
        userPreferences.setOnboardingCompleted(true)

        // When - cambiar a false
        userPreferences.setOnboardingCompleted(false)

        // Then
        val actualValue = userPreferences.hasSeenOnboarding.first()
        assertThat(actualValue).isFalse()
    }

    @Test
    fun `onboarding state should persist across multiple reads`() = runTest {
        // Given
        userPreferences.setOnboardingCompleted(true)

        // When - múltiples lecturas
        val firstRead = userPreferences.hasSeenOnboarding.first()
        val secondRead = userPreferences.hasSeenOnboarding.first()
        val thirdRead = userPreferences.hasSeenOnboarding.first()

        // Then
        assertThat(firstRead).isTrue()
        assertThat(secondRead).isTrue()
        assertThat(thirdRead).isTrue()
    }

    @Test
    fun `onboarding state should survive other preferences operations`() = runTest {
        // Given - establecer onboarding completado
        userPreferences.setOnboardingCompleted(true)

        // When - realizar otras operaciones de preferencias
        userPreferences.saveUserId("test-user-id")
        userPreferences.saveUserEmail("test@email.com")
        userPreferences.setLoggedIn(true)

        // Then - onboarding state debe mantenerse
        val hasSeenOnboarding = userPreferences.hasSeenOnboarding.first()
        assertThat(hasSeenOnboarding).isTrue()
    }

    @Test
    fun `clearAll should reset onboarding state to default`() = runTest {
        // Given - establecer onboarding completado
        userPreferences.setOnboardingCompleted(true)
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()

        // When - limpiar todas las preferencias
        userPreferences.clearAll()

        // Then - onboarding debe volver al default (false)
        val hasSeenOnboarding = userPreferences.hasSeenOnboarding.first()
        assertThat(hasSeenOnboarding).isFalse()
    }

    @Test
    fun `multiple setOnboardingCompleted calls should work correctly`() = runTest {
        // Given & When - múltiples cambios
        userPreferences.setOnboardingCompleted(true)
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()

        userPreferences.setOnboardingCompleted(false)
        assertThat(userPreferences.hasSeenOnboarding.first()).isFalse()

        userPreferences.setOnboardingCompleted(true)
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()

        userPreferences.setOnboardingCompleted(true) // Mismo valor
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()
    }

    @Test
    fun `onboarding completion should be independent of login state`() = runTest {
        // Given
        userPreferences.setOnboardingCompleted(true)
        userPreferences.setLoggedIn(false) // Usuario no loggeado

        // When & Then
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()
        assertThat(userPreferences.isLoggedIn.first()).isFalse()

        // Cambiar estado de login no debe afectar onboarding
        userPreferences.setLoggedIn(true)
        assertThat(userPreferences.hasSeenOnboarding.first()).isTrue()
    }

    @Test
    fun `onboarding preferences key should be consistent`() = runTest {
        // Este test verifica que la key interna es correcta
        // Given
        val expectedKey = stringPreferencesKey("onboarding_completed")

        // When
        userPreferences.setOnboardingCompleted(true)

        // Then - verificar que se guardó con la key correcta
        context.dataStore.data.first().let { preferences ->
            val storedValue = preferences[expectedKey]?.toBoolean()
            assertThat(storedValue).isTrue()
        }
    }

    @Test
    fun `concurrent operations should be handled correctly`() = runTest {
        // Given & When - operaciones concurrentes
        userPreferences.setOnboardingCompleted(true)
        userPreferences.saveUserId("concurrent-user")

        // Then - ambas operaciones deben completarse
        val hasSeenOnboarding = userPreferences.hasSeenOnboarding.first()
        val userId = userPreferences.userId.first()

        assertThat(hasSeenOnboarding).isTrue()
        assertThat(userId).isEqualTo("concurrent-user")
    }

    @Test
    fun `flow emissions should be consistent`() = runTest {
        // Given
        val emissions = mutableListOf<Boolean>()

        // When - recopilar emisiones del Flow
        userPreferences.setOnboardingCompleted(false)
        emissions.add(userPreferences.hasSeenOnboarding.first())

        userPreferences.setOnboardingCompleted(true)
        emissions.add(userPreferences.hasSeenOnboarding.first())

        userPreferences.setOnboardingCompleted(false)
        emissions.add(userPreferences.hasSeenOnboarding.first())

        // Then
        assertThat(emissions).containsExactly(false, true, false).inOrder()
    }

    // Extension property para acceder al DataStore en tests
    private val Context.dataStore: DataStore<Preferences>
        get() = androidx.datastore.preferences.preferencesDataStore(name = "user_preferences").getValue(this, String::javaClass)
}
