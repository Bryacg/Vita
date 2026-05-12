package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.auth.ObtenerUsuarioUseCase
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import com.example.vita.domain.usecase.retos.ActualizarProgresoRetoUseCase
import com.example.vita.domain.usecase.retos.GenerarYGuardarRetosUseCase
import com.example.vita.domain.usecase.retos.ObtenerRetosActivosUseCase
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
    // ✅ Use Cases en lugar de repositorios directos
    private val obtenerRetosActivosUseCase: ObtenerRetosActivosUseCase,
    private val obtenerUsuarioUseCase: ObtenerUsuarioUseCase,
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

                // ✅ UseCase en lugar de challengeRepository directamente
                var retos = obtenerRetosActivosUseCase(uid)

                if (retos.isNotEmpty()) {
                    _uiState.update { it.copy(retos = retos, isLoading = false) }
                    return@launch
                }

                _uiState.update { it.copy(mensajeCarga = "Diseñando tus retos con IA...") }

                // ✅ UseCase en lugar de userRepository directamente
                val user   = obtenerUsuarioUseCase(uid)
                val nombre = user?.name ?: "Entrenador"

                val resultado = generarYGuardarRetosUseCase(uid, nombre)

                _uiState.update {
                    it.copy(
                        retos                = resultado.retos,
                        isLoading            = false,
                        mensajeCarga         = null,
                        retosNuevosGenerados = resultado.fueronGeneradosAhora
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        error        = "Error al cargar retos: ${e.message}",
                        mensajeCarga = null
                    )
                }
            }
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        viewModelScope.launch {
            val uid            = authRepository.getCurrentUserId() ?: return@launch
            val nuevoProgreso  = reto.currentValue + 1
            val estaCompletado = nuevoProgreso >= reto.targetValue
            val nuevoEstado    = if (estaCompletado) "COMPLETED" else "PROGRESSO"

            actualizarProgresoRetoUseCase(
                reto.copy(
                    currentValue = nuevoProgreso.coerceAtMost(reto.targetValue),
                    status       = nuevoEstado
                )
            )
            if (estaCompletado) agregarXpUseCase(uid, 80)
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

    fun onRetosNuevosVisto() {
        _uiState.update { it.copy(retosNuevosGenerados = false) }
    }
}

data class RetosUiState(
    val retos: List<Challenger> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val mensajeCarga: String? = null,
    val retosNuevosGenerados: Boolean = false
)