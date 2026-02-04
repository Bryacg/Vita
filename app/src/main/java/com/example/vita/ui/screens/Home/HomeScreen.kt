package com.example.vita.ui.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vita.ui.components.CardGame
import com.example.vita.ui.components.CardInf
import com.example.vita.ui.screens.ChatBot.ChatBotFab

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 1. Recolectamos el estado unificado (como en tu Perfil)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¡Bienvenido a VitaGame!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // 2. Lógica de UI basada en uiState.isLoading
                if (uiState.isLoading) {
                    // Muestra el indicador mientras 'cargarDatos' hace su magia
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // 3. Si no está cargando, verificamos que los datos no sean nulos
                    val user = uiState.user
                    val progress = uiState.progress

                    if (user != null && progress != null) {
                        CardInf(
                            user = user,
                            progress = progress
                        )
                    } else {
                        // Caso de error: No se encontraron datos tras cargar
                        Text(
                            "Error al cargar datos",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CardGame(
                    titulo = "Minijuego de Velocidad",
                    descripcion = "¡Gana XP actualizando tu progreso!",
                    onclic = {
                        // Al hacer clic, el ViewModel actualiza la DB y llama a cargarDatos()
                        viewModel.ganarExperiencia("GODOT")
                    }
                )
            }
        }

        // Botón del ChatBot siempre visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            ChatBotFab()
        }
    }
}