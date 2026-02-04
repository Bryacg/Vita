package com.example.vita.ui.screens.Profile

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.profile.CardFoodPreferences
import com.example.vita.ui.components.profile.CardUser
import com.example.vita.ui.components.profile.SeccionRecordatoriosHabitos
import com.example.vita.ui.components.profile.perfilBiometrico

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lanzador para solicitar permisos de notificación (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Opcional: Manejar si se rechaza el permiso
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            // 1. Identidad
            CardUser(user = uiState.user, profile = uiState.profile)

            // 2. Biometría
            perfilBiometrico(
                profile = uiState.profile,
                onGuardar = { peso, altura, edad, gender ->
                    viewModel.guardarDatosFisicos(peso, altura, edad, gender)
                }
            )

            // 3. Preferencias Alimentarias
            CardFoodPreferences(
                preferences = uiState.foodPreferences,
                onAddPreference = { name, type ->
                    viewModel.agregarPreferenciaAlimentaria(name, type)
                },
                onRemovePreference = { preference ->
                    viewModel.eliminarPreferenciaAlimentaria(preference)
                }
            )

            // 4. SECCIÓN DE RECORDATORIOS CORREGIDA
            // Pasamos los valores del uiState para que no se reinicien al navegar
            SeccionRecordatoriosHabitos(
                aguaActivo = uiState.aguaRecordatorioActivo,
                aguaHora = uiState.aguaHora,
                caminarActivo = uiState.caminarRecordatorioActivo,
                caminarHora = uiState.caminarHora,
                onCambioRecordatorio = { tipo, activo, horaStr ->
                    // Solicitar permiso dinámicamente si intenta activar
                    if (activo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }

                    // LLAMADA CORREGIDA: Usamos el nombre que definimos en el ViewModel
                    viewModel.actualizarRecordatorio(tipo, activo, horaStr)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Cerrar Sesión
            Button(
                onClick = {
                    viewModel.cerrarSesion { onLogoutSuccess() }
                },
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