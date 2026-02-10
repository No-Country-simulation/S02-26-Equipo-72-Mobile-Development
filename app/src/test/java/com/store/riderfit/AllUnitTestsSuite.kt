package com.store.riderfit

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Suite completa de tests unitarios para RiderFit
 *
 * Incluye todos los tests de:
 * - Domain Layer (Use Cases)
 * - Presentation Layer (ViewModels, States)
 *
 * Uso:
 * ./gradlew test --tests "com.store.riderfit.AllUnitTestsSuite"
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // ==================== DOMAIN TESTS ====================
    com.store.riderfit.domain.usecase.auth.LoginUseCaseTest::class,
    com.store.riderfit.domain.usecase.auth.RegisterUseCaseTest::class,

    // ==================== PRESENTATION TESTS ====================
    // ViewModels
    com.store.riderfit.presentation.viewmodel.AuthViewModelTest::class,
    com.store.riderfit.presentation.viewmodel.SplashScreenViewModelTest::class,

    // States
    com.store.riderfit.presentation.state.AuthUiStateTest::class,
    com.store.riderfit.presentation.state.SplashStateTest::class,

    // ==================== UTILS TESTS ====================
    com.store.riderfit.utils.validators.EmailValidatorTest::class,
    com.store.riderfit.utils.validators.PasswordValidatorTest::class
)
class AllUnitTestsSuite {

    companion object {
        const val TOTAL_TESTS_EXPECTED = 40 // Aproximado

        /**
         * Categorías de tests incluidas
         */
        val TEST_CATEGORIES = listOf(
            "Authentication Use Cases",
            "ViewModels & State Management",
            "Input Validation",
            "Error Handling",
            "Loading States",
            "Navigation Logic"
        )

        /**
         * Cobertura esperada por módulo
         */
        val COVERAGE_TARGETS = mapOf(
            "Domain Layer" to 90,
            "Presentation Layer" to 85,
            "Utils" to 95
        )
    }
}
