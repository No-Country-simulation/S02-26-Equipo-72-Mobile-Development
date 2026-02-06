package com.store.riderfit.presentation.ui.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    label: String = "Email"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Mail,
                contentDescription = "Email icon"
            )
        },
        trailingIcon = {
            if (isError) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error icon",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = {
            if (isError && errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        singleLine = true,
        enabled = enabled
    )
}

@Preview(showBackground = true)
@Composable
fun EmailFieldPreview() {
    EmailField(
        value = "example@email.com",
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmailFieldErrorPreview() {
    EmailField(
        value = "invalid-email",
        onValueChange = {},
        isError = true,
        errorMessage = "El email no es válido"
    )
}
