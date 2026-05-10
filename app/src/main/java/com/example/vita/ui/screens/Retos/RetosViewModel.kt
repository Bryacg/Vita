package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ChallengeRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import com.example.vita.domain.usecase.retos.ActualizarProgresoRetoUseCase
import com.example.vita.domain.usecase.retos.GenerarYGuardarRetosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RetosViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
    private val generarYGuardarRetosUseCase: GenerarYGuardarRetosUseCase,
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        cargarRetos()
    }

    fun cargarRetos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch

                // Primero intentamos traer retos activos de Room (cualquier día)
                val retosActivos = challengeRepository.getActiveChallenges(uid)

                if (retosActivos.isNotEmpty()) {
                    // Ya hay retos activos: los mostramos sin tocar la IA
                    _uiState.update {
                        it.copy(retos = retosActivos, isLoading = false)
                    }
                    return@launch
                }

                // No hay retos activos → intentamos generar (con protección anti-duplicado de hoy)
                _uiState.update { it.copy(mensajeCarga = "Diseñando tus retos con IA...") }

                val user = userRepository.getUserById(uid)
                val nombre = user?.name ?: "Entrenador"

                val resultado = generarYGuardarRetosUseCase(uid, nombre)

                _uiState.update {
                    it.copy(
                        retos = resultado.retos,
                        isLoading = false,
                        mensajeCarga = null,
                        retosNuevosGenerados = resultado.fueronGeneradosAhora
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar retos: ${e.message}",
                        mensajeCarga = null
                    )
                }
            }
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            val nuevoProgreso = reto.currentValue + 1
            val estaCompletado = nuevoProgreso >= reto.targetValue
            val nuevoEstado = if (estaCompletado) "COMPLETED" else "PROGRESSO"

            val retoActualizado = reto.copy(
                currentValue = nuevoProgreso.coerceAtMost(reto.targetValue),
                status = nuevoEstado
            )

            actualizarProgresoRetoUseCase(retoActualizado)

            if (estaCompletado) {
                agregarXpUseCase(uid, 80)
            }

            cargarRetos()
        }
    }

    fun completarRetoInstantaneo(reto: Challenger) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            actualizarProgresoRetoUseCase(
                reto.copy(currentValue = reto.targetValue, status = "COMPLETED")
            )
            agregarXpUseCase(uid, 80)
            cargarRetos()
        }
    }

    // Llamado desde la UI tras mostrar el snackbar de "¡Retos nuevos!"
    fun onRetosNuevosVisto() {
        _uiState.update { it.copy(retosNuevosGenerados = false) }
    }
}

data class RetosUiState(
    val retos: List<Challenger> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val mensajeCarga: String? = null,
    val retosNuevosGenerados: Boolean = false // para mostrar un snackbar opcional
)