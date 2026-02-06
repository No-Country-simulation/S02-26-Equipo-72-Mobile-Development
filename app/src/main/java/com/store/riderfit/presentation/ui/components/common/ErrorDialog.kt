package com.store.riderfit.presentation.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErrorDialog(
    title: String = "Error",
    message: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    confirmButtonText: String = "OK"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm ?: onDismiss) {
                Text(confirmButtonText)
            }
        },
        dismissButton = if (onConfirm != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        } else {
            null
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(
        title = "Error de autenticación",
        message = "El email o contraseña son incorrectos. Por favor, intenta nuevamente.",
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogWithConfirmPreview() {
    ErrorDialog(
        title = "Confirmar acción",
        message = "¿Deseas continuar con esta operación?",
        onDismiss = {},
        onConfirm = {},
        confirmButtonText = "Continuar"
    )
}
