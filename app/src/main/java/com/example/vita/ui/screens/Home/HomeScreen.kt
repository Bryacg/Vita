package com.example.vita.ui.screens.Home

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.vita.ui.components.CardGame
import com.example.vita.ui.components.home.CardAddMealForm
import com.example.vita.ui.components.home.CardFoodSummary
import com.example.vita.ui.components.home.CardInf
import com.example.vita.ui.components.retos.CardRetosD
import com.example.vita.ui.screens.ChatBot.ChatBotFab

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Obtenemos el estado del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ESTADO LOCAL: Controla si mostramos el resumen o el formulario de registro
    var isAddingFood by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Bienvenido a VitaGame!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        // 1. Información de Usuario (Nivel, XP, Perfil)
                        uiState.user?.let { user ->
                            uiState.progress?.let { progress ->
                                CardInf(user = user, progress = progress)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. SECCIÓN DE NUTRICIÓN (Intercambiable)
                        if (!isAddingFood) {
                            // Muestra el resumen de calorías y puntos de salud
                            CardFoodSummary(
                                totalCalories = uiState.totalCaloriesHoy,
                                averageHealthScore = uiState.saludNutricionalHoy,
                                onAddClick = {
                                    // Cambiamos el estado local, NO usamos navController.navigate
                                    isAddingFood = true
                                }
                            )
                        } else {
                            // Muestra el formulario para escribir el nombre y calorías
                            CardAddMealForm(
                                onSave = { nombre, kcal ->
                                    // Llamamos al ViewModel para guardar en la DB
                                    viewModel.registrarNuevaComida(nombre, kcal, 70)
                                    isAddingFood = false // Volvemos al resumen
                                },
                                onCancel = {
                                    isAddingFood = false // Cancelamos y volvemos
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Sección de Retos
                        Text(
                            text = "Retos del día",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        uiState.retoDestacado?.let { reto ->
                            CardRetosD(
                                challenger = reto,
                                onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                                onLongClick = { viewModel.completarRetoInstantaneo(reto) }
                            )
                        } ?: run {
                            Text(
                                text = "No hay retos pendientes.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Acceso al Juego (Godot)
                        CardGame(
                            titulo = "Minijuego de Velocidad",
                            descripcion = "¡Gana XP extra completando tus retos diarios!",
                            onclic = {
                                navController.navigate("game_screen")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Botón flotante del ChatBot
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