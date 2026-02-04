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
import com.example.vita.ui.components.retos.CardRetosD
import com.example.vita.ui.screens.ChatBot.ChatBotFab

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Recolectamos el estado unificado del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp), // Margen lateral consistente
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Bienvenido a VitaGame!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lógica principal de UI según el estado
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null -> {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        // 1. Tarjeta de Información (Nivel, XP, Avatar)
                        uiState.user?.let { user ->
                            uiState.progress?.let { progress ->
                                CardInf(
                                    user = user,
                                    progress = progress
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Sección del Reto Prioritario
                        uiState.retoDestacado?.let { reto ->
                            Text(
                                text = "Reto en curso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Usando tu componente personalizado
                            CardRetosD(
                                challenger = reto,
                                onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                                onLongClick = { viewModel.completarRetoInstantaneo(reto) }
                            )
                        } ?: run {
                            // Si no hay retos disponibles
                            Text(
                                text = "No hay retos pendientes por ahora. ¡Buen trabajo!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Tarjeta de Actividad / Minijuego
                        CardGame(
                            titulo = "Minijuego de Velocidad",
                            descripcion = "¡Gana XP extra completando tus retos diarios!",
                            onclic = {
                                viewModel.ganarExperiencia("GODOT")
                            }
                        )
                    }
                }

                // Espacio extra al final para que el FAB no tape contenido
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // FAB del ChatBot posicionado sobre el contenido
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