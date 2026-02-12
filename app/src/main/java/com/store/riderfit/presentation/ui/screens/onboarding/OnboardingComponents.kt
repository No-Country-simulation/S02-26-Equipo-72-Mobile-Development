package com.store.riderfit.presentation.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.riderfit.R
import com.store.riderfit.presentation.ui.components.auth.AuthButton
import com.store.riderfit.presentation.ui.components.auth.AuthButtonType
import com.store.riderfit.presentation.ui.theme.RiderFitColors

/**
 * Indicadores de página (dots) para el onboarding
 */
@Composable
fun PageIndicators(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage

            val width by animateDpAsState(
                targetValue = if (isSelected)
                    OnboardingData.PageIndicator.ACTIVE_WIDTH_DP.dp
                else
                    OnboardingData.PageIndicator.INACTIVE_WIDTH_DP.dp,
                animationSpec = tween(
                    durationMillis = OnboardingData.ANIMATION_DURATION_MS.toInt()
                ),
                label = "indicator_width"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(OnboardingData.PageIndicator.HEIGHT_DP.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected)
                            RiderFitColors.Primary
                        else
                            RiderFitColors.NeutralTones.L200
                    )
            )

            if (index < totalPages - 1) {
                Spacer(modifier = Modifier.width(OnboardingData.PageIndicator.SPACING_DP.dp))
            }
        }
    }
}

/**
 * Botones de navegación del onboarding
 */
@Composable
fun OnboardingNavigationButtons(
    currentPage: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón Atrás - solo visible si no es la primera página
        AnimatedVisibility(
            visible = currentPage > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.weight(1f)
        ) {
            AuthButton(
                text = OnboardingData.getButtonText(currentPage, isBackButton = true),
                onClick = onPreviousClick,
                type = AuthButtonType.OUTLINED,
                contentColor = RiderFitColors.Primary,
                borderColor = RiderFitColors.Primary
            )
        }

        // Espaciador cuando el botón atrás no está visible
        if (currentPage == 0) {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Botón Siguiente/Empezar
        AuthButton(
            text = OnboardingData.getButtonText(currentPage, isBackButton = false),
            onClick = onNextClick,
            type = AuthButtonType.FILLED,
            backgroundColor = RiderFitColors.Primary,
            contentColor = RiderFitColors.OnPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Contenido de una página individual del onboarding
 */
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen principal con overlay gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay gradiente para mejor legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            ),
            color = RiderFitColors.OnBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            color = RiderFitColors.NeutralTones.L600,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * Layout completo de página del onboarding
 */
@Composable
fun OnboardingPage(
    page: OnboardingPage,
    currentPage: Int,
    totalPages: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RiderFitColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenido principal (ocupa la mayor parte del espacio)
        OnboardingPageContent(
            page = page,
            modifier = Modifier.weight(1f)
        )

        // Indicadores de página
        PageIndicators(
            currentPage = currentPage,
            totalPages = totalPages,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Botones de navegación
        OnboardingNavigationButtons(
            currentPage = currentPage,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
        )
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true)
@Composable
private fun PageIndicatorsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Primera página")
        PageIndicators(currentPage = 0, totalPages = 3)

        Text("Segunda página")
        PageIndicators(currentPage = 1, totalPages = 3)

        Text("Tercera página")
        PageIndicators(currentPage = 2, totalPages = 3)
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingNavigationButtonsFirstPagePreview() {
    OnboardingNavigationButtons(
        currentPage = 0,
        onPreviousClick = { },
        onNextClick = { },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingNavigationButtonsMiddlePagePreview() {
    OnboardingNavigationButtons(
        currentPage = 1,
        onPreviousClick = { },
        onNextClick = { },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingNavigationButtonsLastPagePreview() {
    OnboardingNavigationButtons(
        currentPage = 2,
        onPreviousClick = { },
        onNextClick = { },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingPageContentPreview() {
    OnboardingPageContent(
        page = OnboardingPage(
            imageRes = R.drawable.ic_launcher_foreground, // Placeholder
            title = "¡Elegir equipamiento no debería ser una apuesta!",
            description = "El ajuste correcto depende del caballo, del jinete y del uso real. Cuando algo no encaja, se nota en el rendimiento y en tu bolsillo."
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingPageFullPreview() {
    OnboardingPage(
        page = OnboardingData.pages[0],
        currentPage = 0,
        totalPages = 3,
        onPreviousClick = { },
        onNextClick = { }
    )
}
