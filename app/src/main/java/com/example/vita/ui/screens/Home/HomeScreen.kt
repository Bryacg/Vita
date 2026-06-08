package com.example.vita.ui.screens.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.vita.domain.model.NutritionCategory
import com.example.vita.ui.components.CardGame
import com.example.vita.ui.components.home.CardAddMealForm
import com.example.vita.ui.components.home.CardComidasHoy
import com.example.vita.ui.components.home.CardFoodSummary
import com.example.vita.ui.components.home.HomeHeroHeader
import com.example.vita.ui.components.retos.CardRetosD
import com.example.vita.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    var isAddingFood by remember { mutableStateOf(false) }

    // Auto-oculta el banner de feedback tras 3 segundos
    LaunchedEffect(uiState.ultimaNutricion) {
        if (uiState.ultimaNutricion != null) {
            delay(3_000)
            viewModel.limpiarFeedbackNutricion()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── 1. Cabecera hero ──────────────────────────────────────────────
        // Se muestra siempre, con valores de fallback mientras carga.
        HomeHeroHeader(
            nombreUsuario = uiState.user?.name ?: "Entrenador",
            nivel         = uiState.user?.currentLevel ?: 1,
            xpTotal       = uiState.user?.currentXp ?: 0,
            rachaActual   = uiState.progress?.streakDays ?: 0
        )

        // ── Contenido debajo de la cabecera ───────────────────────────────
        Column(
            modifier            = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                uiState.error != null -> {
                    Text(
                        text     = "Error: ${uiState.error}",
                        color    = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {

                    // 2. Banner de feedback tras registrar comida
                    AnimatedVisibility(
                        visible = uiState.ultimaNutricion != null,
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        uiState.ultimaNutricion?.let { resultado ->
                            val (bgColor, textColor) = when (resultado.nutritionCategory) {
                                NutritionCategory.MUY_SALUDABLE ->
                                    Color(0xFF2E7D32) to Color.White
                                NutritionCategory.SALUDABLE ->
                                    Color(0xFF4CAF50) to Color.White
                                NutritionCategory.REGULAR ->
                                    Color(0xFFF2994A) to Color.White
                                NutritionCategory.POCO_SANO ->
                                    Color(0xFFC62828) to Color.White
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                color    = bgColor,
                                shape    = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    modifier              = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text       = resultado.category,
                                            style      = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color      = textColor
                                        )
                                        if (resultado.isOverCalorieLimit) {
                                            Text(
                                                text  = "Superaste el límite de ${com.example.vita.domain.model.GameConfig.LIMITE_CALORIAS_INGESTA} kcal",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = textColor.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                    Text(
                                        text       = "+${resultado.xpEarned} XP",
                                        style      = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color      = textColor
                                    )
                                }
                            }
                        }
                    }

                    // 3. Nutrición — resumen o formulario
                    if (!isAddingFood) {
                        CardFoodSummary(
                            totalCalories      = uiState.totalCaloriesHoy,
                            averageHealthScore = uiState.saludNutricionalHoy,
                            onAddClick         = { isAddingFood = true }
                        )
                    } else {
                        CardAddMealForm(
                            onSave   = { nombre, kcal ->
                                viewModel.registrarNuevaComida(nombre, kcal)
                                isAddingFood = false
                            },
                            onCancel = { isAddingFood = false }
                        )
                    }

                    // 4. Lista de comidas del día
                    if (uiState.comidasHoy.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CardComidasHoy(
                            comidas    = uiState.comidasHoy,
                            onEliminar = { mealId -> viewModel.eliminarComida(mealId) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Reto del día
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

                    // 6. Minijuego
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
}