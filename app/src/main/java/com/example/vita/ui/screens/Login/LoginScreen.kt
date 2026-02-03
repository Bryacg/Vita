package com.example.vita.ui.screens.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToCreateAccount: () -> Unit,
    onLoginAttempt: (String, String) -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            if (uiState.navigateToProfile) {
                onNavigateToProfile()
            } else {
                onLoginSuccess()
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primaryContainer) // Color de fondo basado en el tema
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface, // Usa el color de superficie del tema
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineSmall, // Tipografía del tema
                    color = MaterialTheme.colorScheme.onSurface // Color de texto adaptativo
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                Button(
                    onClick = { onLoginAttempt(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    // Cambiado de Color fijo a Primary del tema
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    Text("Iniciar Sesión")
                }

                OutlinedButton(
                    onClick = { viewModel.loginConGoogle(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified // Mantiene los colores originales del logo
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", color = MaterialTheme.colorScheme.onSurface)
                }

                TextButton(
                    onClick = onNavigateToCreateAccount,
                    enabled = !uiState.isLoading
                ) {
                    Text("Registrarse", color = MaterialTheme.colorScheme.secondary)
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error, // Color de error del tema
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}