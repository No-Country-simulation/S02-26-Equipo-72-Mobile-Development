package com.store.riderfit.presentation.ui.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.store.riderfit.presentation.ui.theme.RiderFitColors

enum class AuthButtonType {
    FILLED,
    OUTLINED
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: AuthButtonType = AuthButtonType.FILLED,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = RiderFitColors.Primary,
    contentColor: Color = RiderFitColors.OnPrimary,
    borderColor: Color = RiderFitColors.Primary
) {
    when (type) {
        AuthButtonType.FILLED -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = enabled && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor,
                    disabledContainerColor = RiderFitColors.NeutralTones.L100,
                    disabledContentColor = RiderFitColors.NeutralTones.L400
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp,
                    disabledElevation = 0.dp
                )
            ) {
                AuthButtonContent(
                    text = text,
                    isLoading = isLoading,
                    contentColor = contentColor,
                    enabled = enabled
                )
            }
        }

        AuthButtonType.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = enabled && !isLoading,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (enabled) borderColor else RiderFitColors.NeutralTones.L200
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = if (enabled) contentColor else RiderFitColors.NeutralTones.L400,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = RiderFitColors.NeutralTones.L400
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) {
                AuthButtonContent(
                    text = text,
                    isLoading = isLoading,
                    contentColor = if (enabled) contentColor else RiderFitColors.NeutralTones.L400,
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun AuthButtonContent(
    text: String,
    isLoading: Boolean,
    contentColor: Color,
    enabled: Boolean
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.height(24.dp),
            color = contentColor,
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = contentColor
        )
    }
}

// ===== PREVIEWS =====

@Preview(showBackground = true)
@Composable
fun AuthButtonFilledPreview() {
    AuthButton(
        text = "Crear mi cuenta",
        onClick = {},
        type = AuthButtonType.FILLED
    )
}

@Preview(showBackground = true)
@Composable
fun AuthButtonOutlinedPreview() {
    AuthButton(
        text = "Registrarse",
        onClick = {},
        type = AuthButtonType.OUTLINED,
        contentColor = RiderFitColors.Primary
    )
}

@Preview(showBackground = true)
@Composable
fun AuthButtonLoadingPreview() {
    AuthButton(
        text = "Crear mi cuenta",
        onClick = {},
        isLoading = true
    )
}

@Preview(showBackground = true)
@Composable
fun AuthButtonDisabledPreview() {
    AuthButton(
        text = "Crear mi cuenta",
        onClick = {},
        enabled = false
    )
}

@Preview(showBackground = true)
@Composable
fun AuthButtonSecondaryPreview() {
    AuthButton(
        text = "Invitado",
        onClick = {},
        backgroundColor = RiderFitColors.Secondary,
        contentColor = RiderFitColors.OnSecondary
    )
}

@Preview(showBackground = true)
@Composable
fun AuthButtonOutlinedSecondaryPreview() {
    AuthButton(
        text = "Otro botón",
        onClick = {},
        type = AuthButtonType.OUTLINED,
        borderColor = RiderFitColors.Secondary,
        contentColor = RiderFitColors.Secondary
    )
}
