package com.store.riderfit.presentation.ui.screens.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.store.riderfit.R
import com.store.riderfit.presentation.ui.components.auth.AuthButton
import com.store.riderfit.presentation.ui.components.auth.AuthButtonType
import com.store.riderfit.presentation.ui.theme.RiderFitColors

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen en la parte superior - 1/4 de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.auth_riderfit),
                    contentDescription = "Fondo ecuestre",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay degradado ligero
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.2f)
                                )
                            )
                        )
                )
            }

            // Contenido principal - 3/4 de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Título principal
                Text(
                    text = "¡Sé Bienvenido a RiderFit,\nEmpieza a gestionar tu equipamiento ecuestre y descubre soluciones diseñadas para rendir de verdad!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 28.sp
                    ),
                    color = RiderFitColors.NeutralTones.L900,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtítulo
                Text(
                    text = "Explora artículos elegidos por lo que realmente importa: Ajuste, Resistencia y Comodidad en el uso diario.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = RiderFitColors.NeutralTones.L600,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                // Sección de botones
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón Crear mi cuenta
                    AuthButton(
                        text = "Crear mi cuenta",
                        onClick = { navController.navigate("register") },
                        type = AuthButtonType.FILLED,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Botón Iniciar sesión
                    AuthButton(
                        text = "Iniciar sesión",
                        onClick = { navController.navigate("login") },
                        type = AuthButtonType.OUTLINED,
                        contentColor = RiderFitColors.Primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Enlace olvidó contraseña
                    TextButton(
                        onClick = { /* TODO: Implementar recuperación de contraseña */ },
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "¿Olvidó su contraseña?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RiderFitColors.Primary
                        )
                    }

                    // Separador
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = RiderFitColors.NeutralTones.L300
                        )
                        Text(
                            text = " o ingresa como ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RiderFitColors.NeutralTones.L600,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = RiderFitColors.NeutralTones.L300
                        )
                    }

                    // Botón Invitado
                    AuthButton(
                        text = "Invitado",
                        onClick = {
                            // Navegar como invitado
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        type = AuthButtonType.FILLED,
                        backgroundColor = RiderFitColors.Secondary,
                        contentColor = RiderFitColors.OnSecondary,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Footer legal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Al iniciar aceptas las condiciones del servicio de RiderFit. Nos tomamos muy en serio tu privacidad. Para más información lee nuestra",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            ),
                            color = RiderFitColors.NeutralTones.L600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        TextButton(
                            onClick = { /* TODO: Abrir política de privacidad */ },
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                text = "Política de privacidad",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp
                                ),
                                color = RiderFitColors.Primary
                            )
                        }
                    }
                }
            }
        }
    }
}
