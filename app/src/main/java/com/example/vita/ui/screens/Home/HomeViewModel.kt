package com.example.vita.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.Meal
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.*
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val progresoRepository: ProgresoRepository,
    private val challengeRepository: ChallengeRepository,
    private val mealRepository: MealRepository,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId = authRepository.getCurrentUserId()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.getUserById(uid)
                var progress = progresoRepository.getProgreso(uid)

                if (progress == null) {
                    val nuevoProgreso = Progress(
                        id = 0, userId = uid, level = 1, xp = 0,
                        streakDays = 1, bmi = 0f, weight = 0f,
                        date = System.currentTimeMillis()
                    )
                    progresoRepository.insertarProgreso(nuevoProgreso)
                    progress = nuevoProgreso
                }

                // Uso de getMealsByDate tal cual está en tu interfaz
                val comidasHoy = mealRepository.getMealsByDate(uid, System.currentTimeMillis())
                val totalKcal = comidasHoy.sumOf { it.calories }
                val promedioSalud = if (comidasHoy.isNotEmpty()) {
                    comidasHoy.map { it.healthyScore }.average().toInt()
                } else 0

                val todosLosRetos = challengeRepository.getActiveChallenges(uid)
                val retoPrioritario = todosLosRetos
                    .find { it.currentValue > 0 && it.status != "COMPLETED" }
                    ?: todosLosRetos.find { it.status != "COMPLETED" }

                _uiState.update { it.copy(
                    user = user,
                    progress = progress,
                    retoDestacado = retoPrioritario,
                    totalCaloriesHoy = totalKcal,
                    saludNutricionalHoy = promedioSalud,
                    isLoading = false
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun registrarNuevaComida(nombre: String, kcal: Int, salud: Int) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                // CORRECCIÓN: insertMeal según tu interfaz
                val nuevaComida = Meal(
                    id = 0,
                    userId = uid,
                    name = nombre,
                    calories = kcal,
                    healthyScore = salud,
                    date = System.currentTimeMillis()
                )
                mealRepository.insertMeal(nuevaComida)
                cargarDatos()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun ganarExperiencia(tipo: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            val puntos = if (tipo.uppercase() == "GODOT") 170 else 80
            agregarXpUseCase(uid, puntos)
            cargarDatos()
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            val nuevoProgreso = (reto.currentValue + 1).coerceAtMost(reto.targetValue)
            val estaCompletado = nuevoProgreso >= reto.targetValue
            challengeRepository.updateReto(reto.copy(
                currentValue = nuevoProgreso,
                status = if (estaCompletado) "COMPLETED" else "PROGRESSO"
            ))
            if (estaCompletado) agregarXpUseCase(uid, 80)
            cargarDatos()
        }
    }

    fun completarRetoInstantaneo(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            challengeRepository.updateReto(reto.copy(currentValue = reto.targetValue, status = "COMPLETED"))
            agregarXpUseCase(uid, 80)
            cargarDatos()
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val progress: Progress? = null,
    val retoDestacado: Challenger? = null,
    val totalCaloriesHoy: Int = 0,
    val saludNutricionalHoy: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)