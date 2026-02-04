package com.example.vita.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.repository.ChallengeRepository
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
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId = authRepository.getCurrentUserId()

    init {
        cargarDatos()
    }

    /**
     * Carga de forma unificada el usuario, su progreso global y el reto prioritario.
     */
    fun cargarDatos() {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Obtener datos básicos
                val user = userRepository.getUserById(uid)
                var progress = progresoRepository.getProgreso(uid)

                // Si no existe progreso inicial (primer inicio), se crea
                if (progress == null) {
                    val nuevoProgreso = Progress(
                        id = 0, userId = uid, level = 1, xp = 0,
                        streakDays = 1, bmi = 0f, weight = 0f,
                        date = System.currentTimeMillis()
                    )
                    progresoRepository.insertarProgreso(nuevoProgreso)
                    progress = nuevoProgreso
                }

                // 2. Obtener lista de retos para seleccionar el prioritario
                val todosLosRetos = challengeRepository.getActiveChallenges(uid)

                // Prioridad:
                // A) El primero que tenga progreso real pero no esté completado.
                // B) Si no hay ninguno empezado, el primero que esté activo.
                val retoPrioritario = todosLosRetos
                    .find { it.currentValue > 0 && it.status != "COMPLETED" }
                    ?: todosLosRetos.find { it.status != "COMPLETED" }

                _uiState.update { it.copy(
                    user = user,
                    progress = progress,
                    retoDestacado = retoPrioritario,
                    isLoading = false
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Lógica para el minijuego o botones de prueba de XP.
     */
    fun ganarExperiencia(tipo: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            val puntos = when (tipo.uppercase()) {
                "GODOT" -> 170
                "DIARIO" -> 80
                else -> 0
            }

            if (puntos > 0) {
                agregarXpUseCase(uid, puntos)
                cargarDatos() // Refresca para actualizar la CardInf y Nivel
            }
        }
    }

    /**
     * Permite actualizar el reto desde la CardRetosD de la Home.
     */
    fun actualizarProgresoReto(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            val nuevoProgreso = reto.currentValue + 1
            val estaCompletado = nuevoProgreso >= reto.targetValue
            val nuevoEstado = if (estaCompletado) "COMPLETED" else "PROGRESSO"

            val retoActualizado = reto.copy(
                currentValue = nuevoProgreso.coerceAtMost(reto.targetValue),
                status = nuevoEstado
            )

            // Actualizamos en DB de retos
            challengeRepository.updateReto(retoActualizado)

            // Si se acaba de completar, enviamos XP
            if (estaCompletado) {
                agregarXpUseCase(uid, 80)
            }

            // Recargamos datos para que la Home refleje el cambio (CardInf y desaparición de reto)
            cargarDatos()
        }
    }

    /**
     * Fuerza el completado del reto (LongClick).
     */
    fun completarRetoInstantaneo(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            val retoCompletado = reto.copy(
                currentValue = reto.targetValue,
                status = "COMPLETED"
            )
            challengeRepository.updateReto(retoCompletado)
            agregarXpUseCase(uid, 80)
            cargarDatos()
        }
    }
}

/**
 * Estado unificado para la HomeScreen
 */
data class HomeUiState(
    val user: User? = null,
    val progress: Progress? = null,
    val retoDestacado: Challenger? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)