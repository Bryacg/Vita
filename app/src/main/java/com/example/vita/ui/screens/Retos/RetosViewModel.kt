package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.retos.*
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase // Asegúrate de que esta ruta sea correcta
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
    private val repository: com.example.vita.domain.repository.ChallengeRepository,
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() { // Corregido: Heredamos correctamente de ViewModel

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        cargarRetos()
    }

    fun cargarRetos() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val retosDB = repository.getActiveChallenges(uid)

                val listaNueva = retosDB.map { it.copy() }.toList()

                _uiState.update { it.copy(
                    retos = listaNueva,
                    isLoading = false
                ) }
                android.util.Log.d("VITA_LOG", "Lista refrescada: ${listaNueva.size} ítems")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
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

            // Guardamos el progreso del reto
            actualizarProgresoRetoUseCase(retoActualizado)

            // Si se completa, disparamos el UseCase de XP que actualiza 'progress' y 'users'
            if (estaCompletado) {
                val puntos = 80
                agregarXpUseCase(uid, puntos)
                android.util.Log.d("VITA_LOG", "XP enviada: $puntos puntos por completar reto")
            }

            cargarRetos()
        }
    }

    fun completarRetoInstantaneo(reto: Challenger) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            val retoCompletado = reto.copy(
                currentValue = reto.targetValue,
                status = "COMPLETED"
            )

            actualizarProgresoRetoUseCase(retoCompletado)

            // Enviamos XP al completar instantáneamente
            val puntos = 80
            agregarXpUseCase(uid, puntos)
            android.util.Log.d("VITA_LOG", "XP enviada: $puntos puntos por reto instantáneo")

            cargarRetos()
        }
    }
}

data class RetosUiState(
    val retos: List<Challenger> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)