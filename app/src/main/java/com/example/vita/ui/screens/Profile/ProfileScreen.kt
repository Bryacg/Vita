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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vita.ui.components.profile.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit,
    onPerfilCompletado: () -> Unit = {}        // nuevo callback
) {
    val uiState by viewModel.uiState.collectAsState()

    // Escucha el evento de navegación del ViewModel
    LaunchedEffect(Unit) {
        viewModel.navegarAHome.collect {
            onPerfilCompletado()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {

            // Banner de bienvenida solo en el primer acceso
            if (uiState.esPrimerAcceso) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color  = MaterialTheme.colorScheme.primaryContainer,
                    shape  = MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text       = "Bienvenido a VitaGame",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Completa tu perfil biométrico para personalizar tus retos y recomendaciones.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            CardUser(
                user        = uiState.user,
                profile     = uiState.profile,
                rachaActual = uiState.rachaActual
            )

            if (uiState.logros.isNotEmpty()) {
                SeccionLogros(logros = uiState.logros)
            }

            PerfilBiometrico(
                profile   = uiState.profile,
                onGuardar = { peso, altura, edad, gender ->
                    viewModel.guardarDatosFisicos(peso, altura, edad, gender)
                }
            )

            CardFoodPreferences(
                preferences        = uiState.foodPreferences,
                onAddPreference    = { name, type ->
                    viewModel.agregarPreferenciaAlimentaria(name, type)
                },
                onRemovePreference = { viewModel.eliminarPreferenciaAlimentaria(it) }
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
                onClick  = { viewModel.cerrarSesion { onLogoutSuccess() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}