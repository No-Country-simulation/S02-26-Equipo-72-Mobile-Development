package com.store.riderfit.presentation.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.riderfit.presentation.ui.theme.RiderFitTheme
import com.store.riderfit.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal del onboarding con ViewPager
 *
 * Características:
 * - Navegación por gestos swipe
 * - Indicadores de página animados
 * - Botones de navegación
 * - Integración con ViewModel
 * - Transición automática a login al completar
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingCompleted: (Boolean) -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // PagerState para controlar el ViewPager
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { OnboardingData.TOTAL_PAGES }
    )

    // Sincronizar el estado del pager con el ViewModel
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    // Sincronizar el ViewModel con el estado del pager (para gestos swipe)
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            viewModel.navigateToPage(pagerState.currentPage)
        }
    }

    // Detectar cuando se completa el onboarding
    // Navegar cuando el onboarding esté completado
    LaunchedEffect(uiState.hasSeenOnboarding) {
        if (uiState.hasSeenOnboarding) {
            onOnboardingCompleted(uiState.isGuestUser)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = com.store.riderfit.presentation.ui.theme.RiderFitColors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = OnboardingData.pages.getOrNull(pageIndex)

                if (page != null) {
                    OnboardingPage(
                        page = page,
                        currentPage = uiState.currentPage,
                        totalPages = OnboardingData.TOTAL_PAGES,
                        onPreviousClick = {
                            if (!uiState.isAnimating) {
                                coroutineScope.launch {
                                    viewModel.navigateToPrevious()
                                }
                            }
                        },
                        onNextClick = {
                            if (!uiState.isAnimating) {
                                coroutineScope.launch {
                                    if (page.isLast) {
                                        // Última página - completar onboarding
                                        viewModel.navigateToNext() // Esto trigger hasSeenOnboarding
                                    } else {
                                        // Navegar a siguiente página
                                        viewModel.navigateToNext()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Versión simplificada sin ViewModel para testing/preview
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreenStateless(
    currentPage: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    onOnboardingCompleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { OnboardingData.TOTAL_PAGES }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentPage) {
            onPageChanged(pagerState.currentPage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = com.store.riderfit.presentation.ui.theme.RiderFitColors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = OnboardingData.pages.getOrNull(pageIndex)

                if (page != null) {
                    OnboardingPage(
                        page = page,
                        currentPage = currentPage,
                        totalPages = OnboardingData.TOTAL_PAGES,
                        onPreviousClick = {
                            coroutineScope.launch {
                                val newPage = (currentPage - 1).coerceAtLeast(0)
                                pagerState.animateScrollToPage(newPage)
                                onPageChanged(newPage)
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                if (page.isLast) {
                                    onOnboardingCompleted()
                                } else {
                                    val newPage = (currentPage + 1).coerceAtMost(OnboardingData.TOTAL_PAGES - 1)
                                    pagerState.animateScrollToPage(newPage)
                                    onPageChanged(newPage)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    RiderFitTheme {
        OnboardingScreenStateless(
            currentPage = 0,
            onPageChanged = { },
            onOnboardingCompleted = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenMiddlePagePreview() {
    RiderFitTheme {
        OnboardingScreenStateless(
            currentPage = 1,
            onPageChanged = { },
            onOnboardingCompleted = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenLastPagePreview() {
    RiderFitTheme {
        OnboardingScreenStateless(
            currentPage = 2,
            onPageChanged = { },
            onOnboardingCompleted = { }
        )
    }
}
