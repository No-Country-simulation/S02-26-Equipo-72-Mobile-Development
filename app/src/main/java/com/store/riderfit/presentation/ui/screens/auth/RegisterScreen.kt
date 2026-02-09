package com.store.riderfit.presentation.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.store.riderfit.R
import com.store.riderfit.presentation.ui.components.auth.AuthButton
import com.store.riderfit.presentation.ui.components.auth.AuthButtonType
import com.store.riderfit.presentation.ui.components.auth.EmailField
import com.store.riderfit.presentation.ui.components.auth.PasswordField
import com.store.riderfit.presentation.ui.components.common.ErrorDialog
import com.store.riderfit.presentation.ui.theme.RiderFitColors
import com.store.riderfit.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Validar si todos los campos están completos y válidos
    val isFormValid = remember(uiState.displayName, uiState.email, uiState.password, uiState.passwordConfirm,
                              uiState.displayNameError, uiState.emailError, uiState.passwordError, uiState.confirmPasswordError) {
        uiState.displayName.isNotBlank() &&
        uiState.email.isNotBlank() &&
        uiState.password.isNotBlank() &&
        uiState.passwordConfirm.isNotBlank() &&
        uiState.displayNameError.isNullOrEmpty() &&
        uiState.emailError.isNullOrEmpty() &&
        uiState.passwordError.isNullOrEmpty() &&
        uiState.confirmPasswordError.isNullOrEmpty() &&
        uiState.password == uiState.passwordConfirm
    }

    // Navegar a home después de registro exitoso
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !uiState.isSubmitting) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    if (uiState.error != null) {
        ErrorDialog(
            title = "Error en el registro",
            message = uiState.error ?: "Error desconocido",
            onDismiss = { viewModel.clearErrors() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón back arriba a la izquierda
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = RiderFitColors.NeutralTones.L600
                )
            }
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo_riderfit),
            contentDescription = "RiderFit Logo",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            colorFilter = ColorFilter.tint(RiderFitColors.Primary)
        )

        // Título
        Text(
            text = "Crea tu cuenta y empieza a organizar tu equipamiento.",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 24.sp
            ),
            color = RiderFitColors.NeutralTones.L900,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Descripción
        Text(
            text = "Con tu cuenta podrás guardar productos, revisar pedidos, llevar el control de tu equipamiento y acceder a recomendaciones según tus necesidades en un solo lugar.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            color = RiderFitColors.NeutralTones.L600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Formulario
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Campo Nombre completo
            Text(
                text = "Nombre completo *",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = RiderFitColors.NeutralTones.L700,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = { viewModel.onDisplayNameChanged(it) },
                placeholder = {
                    Text(
                        text = "Sebastián Cáceres Valencia",
                        color = RiderFitColors.NeutralTones.L400
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = !uiState.displayNameError.isNullOrEmpty(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RiderFitColors.Primary,
                    unfocusedBorderColor = RiderFitColors.NeutralTones.L300,
                    errorBorderColor = RiderFitColors.Error
                ),
                trailingIcon = {
                    if (uiState.displayName.isNotBlank() && uiState.displayNameError.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.checkbox_on_background),
                            contentDescription = "Válido",
                            tint = RiderFitColors.Primary
                        )
                    }
                }
            )

            Text(
                text = "Requerimiento aceptado",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (uiState.displayName.isNotBlank() && uiState.displayNameError.isNullOrEmpty())
                    RiderFitColors.Primary else RiderFitColors.NeutralTones.L400,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo Correo Electrónico
            Text(
                text = "Correo Electrónico *",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = RiderFitColors.NeutralTones.L700,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = {
                    Text(
                        text = "sebastiancaceres16@gmail.com",
                        color = RiderFitColors.NeutralTones.L400
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = !uiState.emailError.isNullOrEmpty(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RiderFitColors.Primary,
                    unfocusedBorderColor = RiderFitColors.NeutralTones.L300,
                    errorBorderColor = RiderFitColors.Error
                ),
                trailingIcon = {
                    if (uiState.email.isNotBlank() && uiState.emailError.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.checkbox_on_background),
                            contentDescription = "Válido",
                            tint = RiderFitColors.Primary
                        )
                    }
                }
            )

            Text(
                text = if (!uiState.emailError.isNullOrEmpty()) uiState.emailError!!
                      else "Sensible a mayús & minus, puede contener números, caracteres.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (!uiState.emailError.isNullOrEmpty()) RiderFitColors.Error
                       else if (uiState.email.isNotBlank() && uiState.emailError.isNullOrEmpty())
                           RiderFitColors.Primary else RiderFitColors.NeutralTones.L400,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo Contraseña
            Text(
                text = "Contraseña *",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = RiderFitColors.NeutralTones.L700,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = {
                    Text(
                        text = "sebastian@caceres16",
                        color = RiderFitColors.NeutralTones.L400
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = !uiState.passwordError.isNullOrEmpty(),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RiderFitColors.Primary,
                    unfocusedBorderColor = RiderFitColors.NeutralTones.L300,
                    errorBorderColor = RiderFitColors.Error
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) android.R.drawable.ic_menu_view
                                else android.R.drawable.ic_menu_close_clear_cancel
                            ),
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = RiderFitColors.NeutralTones.L600
                        )
                    }
                }
            )

            Text(
                text = "Sensible a mayús & minus, pueden contener números, signos, caracteres.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (uiState.password.isNotBlank() && uiState.passwordError.isNullOrEmpty())
                    RiderFitColors.Primary else RiderFitColors.NeutralTones.L400,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo Confirmar contraseña
            Text(
                text = "Confirmar contraseña *",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = RiderFitColors.NeutralTones.L700,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var confirmPasswordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.passwordConfirm,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                placeholder = {
                    Text(
                        text = "sebastian@caceres16",
                        color = RiderFitColors.NeutralTones.L400
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = !uiState.confirmPasswordError.isNullOrEmpty() ||
                          (uiState.passwordConfirm.isNotBlank() && uiState.password != uiState.passwordConfirm),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RiderFitColors.Primary,
                    unfocusedBorderColor = RiderFitColors.NeutralTones.L300,
                    errorBorderColor = RiderFitColors.Error
                ),
                trailingIcon = {
                    Row {
                        if (uiState.passwordConfirm.isNotBlank() &&
                            uiState.password == uiState.passwordConfirm &&
                            uiState.confirmPasswordError.isNullOrEmpty()) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.checkbox_on_background),
                                contentDescription = "Válido",
                                tint = RiderFitColors.Primary
                            )
                        }
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (confirmPasswordVisible) android.R.drawable.ic_menu_view
                                    else android.R.drawable.ic_menu_close_clear_cancel
                                ),
                                contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                                tint = RiderFitColors.NeutralTones.L600
                            )
                        }
                    }
                }
            )

            Text(
                text = "Sensible a mayús & minus, pueden contener números, signos, caracteres.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (uiState.passwordConfirm.isNotBlank() &&
                          uiState.password == uiState.passwordConfirm &&
                          uiState.confirmPasswordError.isNullOrEmpty())
                    RiderFitColors.Primary else RiderFitColors.NeutralTones.L400,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // Botón Crear mi cuenta
        AuthButton(
            text = "Crear mi cuenta",
            onClick = {
                viewModel.register(uiState.email, uiState.password, uiState.displayName)
            },
            type = AuthButtonType.FILLED,
            isLoading = uiState.isLoading,
            enabled = isFormValid && !uiState.isLoading,
            backgroundColor = if (isFormValid) RiderFitColors.Primary else RiderFitColors.NeutralTones.L200,
            contentColor = if (isFormValid) RiderFitColors.OnPrimary else RiderFitColors.NeutralTones.L500,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Enlace a Login
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes una cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = RiderFitColors.NeutralTones.L600
            )
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = RiderFitColors.Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer legal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Al iniciar aceptas las ",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = RiderFitColors.NeutralTones.L600
                )
                TextButton(
                    onClick = { /* TODO: Abrir condiciones */ },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "condiciones del servicio de RiderFit",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RiderFitColors.Primary
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ". Nos tomamos muy en serio tu privacidad. Para más información lee nuestra ",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = RiderFitColors.NeutralTones.L600
                )
                TextButton(
                    onClick = { /* TODO: Abrir política */ },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "Política de privacidad",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = RiderFitColors.Primary
                    )
                }
            }
        }
    }
}
