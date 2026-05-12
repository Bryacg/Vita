package com.example.vita.ui.screens.Profile

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.profile.CardFoodPreferences
import com.example.vita.ui.components.profile.CardUser
import com.example.vita.ui.components.profile.PerfilBiometrico  // ✅ nombre actualizado
import com.example.vita.ui.components.profile.SeccionLogros
import com.example.vita.ui.components.profile.SeccionRecordatoriosHabitos

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Manejar resultado */ }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            CardUser(user = uiState.user, profile = uiState.profile)

            if (uiState.logros.isNotEmpty()) {
                SeccionLogros(logros = uiState.logros)
            }

            // ✅ Nombre actualizado: PerfilBiometrico (con mayúscula)
            PerfilBiometrico(
                profile   = uiState.profile,
                onGuardar = { peso, altura, edad, gender ->
                    viewModel.guardarDatosFisicos(peso, altura, edad, gender)
                }
            )

            CardFoodPreferences(
                preferences       = uiState.foodPreferences,
                onAddPreference   = { name, type ->
                    viewModel.agregarPreferenciaAlimentaria(name, type)
                },
                onRemovePreference = { preference ->
                    viewModel.eliminarPreferenciaAlimentaria(preference)
                }
            )

            SeccionRecordatoriosHabitos(
                aguaActivo           = uiState.aguaRecordatorioActivo,
                aguaHora             = uiState.aguaHora,
                caminarActivo        = uiState.caminarRecordatorioActivo,
                caminarHora          = uiState.caminarHora,
                onCambioRecordatorio = { tipo, activo, horaStr ->
                    if (activo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    viewModel.actualizarRecordatorio(tipo, activo, horaStr)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.cerrarSesion { onLogoutSuccess() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}