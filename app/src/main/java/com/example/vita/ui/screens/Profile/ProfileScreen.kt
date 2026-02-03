package com.example.vita.ui.screens.Profile

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
import com.example.vita.ui.components.profile.perfilBiometrico

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            // 1. Identidad: Quién soy, nivel y progreso
            CardUser(user = uiState.user, profile = uiState.profile)

            // 2. Biometría: Mis datos físicos (Suele ir primero por relevancia)
            perfilBiometrico(
                profile = uiState.profile,
                onGuardar = { peso, altura, edad, gender ->
                    viewModel.guardarDatosFisicos(
                        peso = peso,
                        altura = altura,
                        edad = edad,
                        genero = gender
                    )
                }
            )

            // 3. Preferencias: Configuración específica de alimentación
            CardFoodPreferences(
                preferences = uiState.foodPreferences,
                onAddPreference = { name, type ->
                    viewModel.agregarPreferenciaAlimentaria(name, type)
                },
                onRemovePreference = { preference ->
                    viewModel.eliminarPreferenciaAlimentaria(preference)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Acción Crítica: Cerrar Sesión
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

            Spacer(modifier = Modifier.height(24.dp)) // Espacio final para que no pegue al borde
        }
    }
}