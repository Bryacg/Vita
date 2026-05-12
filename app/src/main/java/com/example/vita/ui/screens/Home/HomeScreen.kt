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
import com.example.vita.ui.navigation.Routes
import com.example.vita.ui.screens.ChatBot.ChatBotFab

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isAddingFood by remember { mutableStateOf(false) }

    // ✅ Sin Scaffold anidado — el padding ya viene del Scaffold de AppNavigation
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
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

                    // 2. Sección de Nutrición
                    if (!isAddingFood) {
                        CardFoodSummary(
                            totalCalories = uiState.totalCaloriesHoy,
                            averageHealthScore = uiState.saludNutricionalHoy,
                            onAddClick = { isAddingFood = true }
                        )
                    } else {
                        CardAddMealForm(
                            onSave = { nombre, kcal ->
                                viewModel.registrarNuevaComida(nombre, kcal, 70)
                                isAddingFood = false
                            },
                            onCancel = { isAddingFood = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Reto destacado del día
                    Text(
                        text = "Reto del día",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.retoDestacado?.let { reto ->
                        CardRetosD(
                            challenger = reto,
                            onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                            onLongClick = { viewModel.completarRetoInstantaneo(reto) }
                        )
                    } ?: Text(
                        text = "No hay retos pendientes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Card del juego — la ruta correcta es Routes.Juegos
                    CardGame(
                        titulo = "Minijuego de Velocidad",
                        descripcion = "¡Gana XP extra completando tus retos diarios!",
                        onclic = {
                            // ✅ ruta correcta — se conectará al juego en el siguiente sprint
                            navController.navigate(Routes.Juegos.route)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // ChatBot FAB siempre visible en la esquina inferior derecha
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