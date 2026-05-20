package com.example.vita.ui.screens.CreateAcount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.R

@Composable
fun CreateAccountScreen(
    viewModel: CreateAccountViewModel = hiltViewModel(),
    onCreateAccountAttempt: (String, String, String, String) -> Unit,
    onNavigateBackToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,        // cuenta creada, perfil ya existe
    onNavigateToProfile: () -> Unit      // cuenta nueva, necesita perfil
) {
    val uiState by viewModel.uiState.collectAsState()

    var name     by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navega automáticamente cuando el registro es exitoso
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            if (uiState.navigateToProfile) {
                onNavigateToProfile()
            } else {
                onNavigateToHome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter            = painterResource(R.drawable.ic_apple),
                    contentDescription = "Logo",
                    modifier           = Modifier.size(64.dp)
                )
                Text(
                    text  = "VitaGame",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement   = Arrangement.spacedBy(16.dp),
                horizontalAlignment   = Alignment.CenterHorizontally,
                modifier              = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text  = "Crear Cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nombre") },
                    modifier      = Modifier.fillMaxWidth(),
                    enabled       = !uiState.isLoading,
                    singleLine    = true
                )

                OutlinedTextField(
                    value         = lastName,
                    onValueChange = { lastName = it },
                    label         = { Text("Apellido") },
                    modifier      = Modifier.fillMaxWidth(),
                    enabled       = !uiState.isLoading,
                    singleLine    = true
                )

                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Correo Electrónico") },
                    modifier      = Modifier.fillMaxWidth(),
                    enabled       = !uiState.isLoading,
                    singleLine    = true
                )

                OutlinedTextField(
                    value                = password,
                    onValueChange        = { password = it },
                    label                = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier             = Modifier.fillMaxWidth(),
                    enabled              = !uiState.isLoading,
                    singleLine           = true,
                    supportingText       = {
                        Text(
                            "Mínimo 6 caracteres",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick  = {
                        onCreateAccountAttempt(name, lastName, email, password)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled  = !uiState.isLoading
                            && name.isNotBlank()
                            && lastName.isNotBlank()
                            && email.isNotBlank()
                            && password.length >= 6
                ) {
                    Text("Registrarse")
                }

                TextButton(
                    onClick = onNavigateBackToLogin,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text  = "Volver al Login",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (uiState.error != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text     = uiState.error ?: "",
                            color    = MaterialTheme.colorScheme.onErrorContainer,
                            style    = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}