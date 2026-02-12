package com.store.riderfit.presentation.ui.screens.onboarding

import com.google.common.truth.Truth.assertThat
import com.store.riderfit.R
import org.junit.Test

/**
 * Tests unitarios para OnboardingData
 *
 * Valida la configuración estática del onboarding:
 * - Número correcto de páginas
 * - Contenido de cada página
 * - Configuración de indicadores
 * - Textos de botones dinámicos
 */
class OnboardingDataTest {

    @Test
    fun `pages should have correct count`() {
        // Given & When
        val pages = OnboardingData.pages

        // Then
        assertThat(pages).hasSize(OnboardingData.TOTAL_PAGES)
        assertThat(pages).hasSize(3)
    }

    @Test
    fun `first page should have correct content`() {
        // Given
        val firstPage = OnboardingData.pages[0]

        // Then
        assertThat(firstPage.imageRes).isEqualTo(R.drawable.bg_1)
        assertThat(firstPage.title).isEqualTo("¡Elegir equipamiento no debería ser una apuesta!")
        assertThat(firstPage.description).contains("ajuste correcto")
        assertThat(firstPage.description).contains("caballo")
        assertThat(firstPage.description).contains("jinete")
        assertThat(firstPage.isLast).isFalse()
    }

    @Test
    fun `second page should have correct content`() {
        // Given
        val secondPage = OnboardingData.pages[1]

        // Then
        assertThat(secondPage.imageRes).isEqualTo(R.drawable.bg_2)
        assertThat(secondPage.title).isEqualTo("¡Aquí el equipamiento se elige con datos, no al azar!")
        assertThat(secondPage.description).contains("perfil")
        assertThat(secondPage.description).contains("caballo")
        assertThat(secondPage.description).contains("recomendar")
        assertThat(secondPage.isLast).isFalse()
    }

    @Test
    fun `third page should have correct content and be marked as last`() {
        // Given
        val thirdPage = OnboardingData.pages[2]

        // Then
        assertThat(thirdPage.imageRes).isEqualTo(R.drawable.bg_3)
        assertThat(thirdPage.title).isEqualTo("¡Comprar con la seguridad de elegir bien!")
        assertThat(thirdPage.description).contains("equipamiento")
        assertThat(thirdPage.description).contains("ajusta")
        assertThat(thirdPage.description).contains("confianza")
        assertThat(thirdPage.isLast).isTrue()
    }

    @Test
    fun `only last page should be marked as last`() {
        // Given
        val pages = OnboardingData.pages

        // When & Then
        pages.forEachIndexed { index, page ->
            if (index == OnboardingData.TOTAL_PAGES - 1) {
                assertThat(page.isLast).isTrue()
            } else {
                assertThat(page.isLast).isFalse()
            }
        }
    }

    @Test
    fun `all pages should have non-empty titles`() {
        // Given
        val pages = OnboardingData.pages

        // When & Then
        pages.forEach { page ->
            assertThat(page.title).isNotEmpty()
            assertThat(page.title.trim()).isNotEmpty()
        }
    }

    @Test
    fun `all pages should have non-empty descriptions`() {
        // Given
        val pages = OnboardingData.pages

        // When & Then
        pages.forEach { page ->
            assertThat(page.description).isNotEmpty()
            assertThat(page.description.trim()).isNotEmpty()
            assertThat(page.description.length).isGreaterThan(20) // Mínimo contenido útil
        }
    }

    @Test
    fun `all pages should have valid image resources`() {
        // Given
        val pages = OnboardingData.pages
        val expectedImages = listOf(R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3)

        // When & Then
        pages.forEachIndexed { index, page ->
            assertThat(page.imageRes).isEqualTo(expectedImages[index])
            assertThat(page.imageRes).isGreaterThan(0) // ID de recurso válido
        }
    }

    @Test
    fun `getButtonText should return correct text for back button`() {
        // Given & When & Then

        // Primera página - no debe mostrar botón atrás
        assertThat(OnboardingData.getButtonText(0, isBackButton = true)).isEmpty()

        // Páginas intermedias - debe mostrar "Atrás"
        assertThat(OnboardingData.getButtonText(1, isBackButton = true)).isEqualTo("Atrás")
        assertThat(OnboardingData.getButtonText(2, isBackButton = true)).isEqualTo("Atrás")
    }

    @Test
    fun `getButtonText should return correct text for next button`() {
        // Given & When & Then

        // Páginas no-finales - debe mostrar "Siguiente"
        assertThat(OnboardingData.getButtonText(0, isBackButton = false)).isEqualTo("Siguiente")
        assertThat(OnboardingData.getButtonText(1, isBackButton = false)).isEqualTo("Siguiente")

        // Página final - debe mostrar "Empezar"
        assertThat(OnboardingData.getButtonText(2, isBackButton = false)).isEqualTo("Empezar")
    }

    @Test
    fun `isValidPageIndex should validate page indices correctly`() {
        // Given & When & Then

        // Índices válidos
        assertThat(OnboardingData.isValidPageIndex(0)).isTrue()
        assertThat(OnboardingData.isValidPageIndex(1)).isTrue()
        assertThat(OnboardingData.isValidPageIndex(2)).isTrue()

        // Índices inválidos
        assertThat(OnboardingData.isValidPageIndex(-1)).isFalse()
        assertThat(OnboardingData.isValidPageIndex(3)).isFalse()
        assertThat(OnboardingData.isValidPageIndex(100)).isFalse()
    }

    @Test
    fun `page indicator configuration should have valid values`() {
        // Given & When & Then
        assertThat(OnboardingData.PageIndicator.ACTIVE_WIDTH_DP).isEqualTo(24)
        assertThat(OnboardingData.PageIndicator.INACTIVE_WIDTH_DP).isEqualTo(8)
        assertThat(OnboardingData.PageIndicator.HEIGHT_DP).isEqualTo(8)
        assertThat(OnboardingData.PageIndicator.SPACING_DP).isEqualTo(8)

        // Valores deben ser lógicos
        assertThat(OnboardingData.PageIndicator.ACTIVE_WIDTH_DP)
            .isGreaterThan(OnboardingData.PageIndicator.INACTIVE_WIDTH_DP)
        assertThat(OnboardingData.PageIndicator.HEIGHT_DP).isGreaterThan(0)
        assertThat(OnboardingData.PageIndicator.SPACING_DP).isGreaterThan(0)
    }

    @Test
    fun `animation duration should be reasonable`() {
        // Given & When & Then
        assertThat(OnboardingData.ANIMATION_DURATION_MS).isEqualTo(300L)
        assertThat(OnboardingData.ANIMATION_DURATION_MS).isAtLeast(100L) // No muy rápido
        assertThat(OnboardingData.ANIMATION_DURATION_MS).isAtMost(1000L) // No muy lento
    }

    @Test
    fun `total pages constant should match actual pages count`() {
        // Given
        val actualPagesCount = OnboardingData.pages.size

        // When & Then
        assertThat(OnboardingData.TOTAL_PAGES).isEqualTo(actualPagesCount)
        assertThat(OnboardingData.TOTAL_PAGES).isEqualTo(3)
    }

    @Test
    fun `pages should be in logical order`() {
        // Given
        val pages = OnboardingData.pages

        // When & Then
        // Primera página: presentar problema
        assertThat(pages[0].title).contains("apuesta")
        assertThat(pages[0].description).contains("ajuste")

        // Segunda página: presentar solución
        assertThat(pages[1].title).contains("datos")
        assertThat(pages[1].description).contains("perfil")

        // Tercera página: call to action
        assertThat(pages[2].title).contains("seguridad")
        assertThat(pages[2].description).contains("confianza")
    }
}
