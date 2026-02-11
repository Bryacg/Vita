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
import com.example.vita.domain.model.Achievement
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User
import com.example.vita.ui.components.profile.CardFoodPreferences
import com.example.vita.ui.components.profile.CardUser
import com.example.vita.ui.components.profile.SeccionLogros
import com.example.vita.ui.components.profile.SeccionRecordatoriosHabitos
import com.example.vita.ui.components.profile.perfilBiometrico

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
            // 1. Identidad (Avatar, Nivel)
            CardUser(user = uiState.user, profile = uiState.profile)

            // -------------------------------------------------------
            // 2. GALERÍA DE LOGROS (VITRINA DE TROFEOS)
            // -------------------------------------------------------
            // Los ponemos aquí porque son parte de la identidad del jugador
            if (uiState.logros.isNotEmpty()) {
                SeccionLogros(logros = uiState.logros)
            }

            // 3. Biometría (Datos técnicos)
            perfilBiometrico(
                profile = uiState.profile,
                onGuardar = { peso, altura, edad, gender ->
                    viewModel.guardarDatosFisicos(peso, altura, edad, gender)
                }
            )

            // 4. Preferencias Alimentarias
            CardFoodPreferences(
                preferences = uiState.foodPreferences,
                onAddPreference = { name, type ->
                    viewModel.agregarPreferenciaAlimentaria(name, type)
                },
                onRemovePreference = { preference ->
                    viewModel.eliminarPreferenciaAlimentaria(preference)
                }
            )

            // 5. Configuración de Recordatorios
            SeccionRecordatoriosHabitos(
                aguaActivo = uiState.aguaRecordatorioActivo,
                aguaHora = uiState.aguaHora,
                caminarActivo = uiState.caminarRecordatorioActivo,
                caminarHora = uiState.caminarHora,
                onCambioRecordatorio = { tipo, activo, horaStr ->
                    if (activo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    viewModel.actualizarRecordatorio(tipo, activo, horaStr)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Cerrar Sesión
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
