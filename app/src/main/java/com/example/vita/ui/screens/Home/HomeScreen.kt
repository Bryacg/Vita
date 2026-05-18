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
import com.example.vita.ui.components.home.CardComidasHoy
import com.example.vita.ui.components.home.CardFoodSummary
import com.example.vita.ui.components.home.CardInf
import com.example.vita.ui.components.retos.CardRetosD
import com.example.vita.ui.navigation.Routes

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    var isAddingFood by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text       = "¡Bienvenido a VitaGame!",
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(
                    text     = "Error: ${uiState.error}",
                    color    = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {

                // 1. Tarjeta de usuario
                uiState.user?.let { user ->
                    uiState.progress?.let { progress ->
                        CardInf(user = user, progress = progress)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Nutrición — resumen o formulario
                if (!isAddingFood) {
                    CardFoodSummary(
                        totalCalories      = uiState.totalCaloriesHoy,
                        averageHealthScore = uiState.saludNutricionalHoy,
                        onAddClick         = { isAddingFood = true }
                    )
                } else {
                    CardAddMealForm(
                        onSave   = { nombre, kcal, salud ->
                            viewModel.registrarNuevaComida(nombre, kcal, salud)
                            isAddingFood = false
                        },
                        onCancel = { isAddingFood = false }
                    )
                }

                // 3. Lista de comidas del día (se actualiza en tiempo real)
                if (uiState.comidasHoy.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CardComidasHoy(
                        comidas    = uiState.comidasHoy,
                        onEliminar = { mealId -> viewModel.eliminarComida(mealId) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Reto del día
                Text(
                    text       = "Reto del día",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                uiState.retoDestacado?.let { reto ->
                    CardRetosD(
                        challenger    = reto,
                        onUpdateClick = { viewModel.actualizarProgresoReto(reto) },
                        onLongClick   = { viewModel.completarRetoInstantaneo(reto) }
                    )
                } ?: Text(
                    text  = "No hay retos pendientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Minijuego
                CardGame(
                    titulo      = "Minijuego de Velocidad",
                    descripcion = "¡Gana XP extra completando tus retos diarios!",
                    onclic      = { navController.navigate(Routes.Juegos.route) }
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}