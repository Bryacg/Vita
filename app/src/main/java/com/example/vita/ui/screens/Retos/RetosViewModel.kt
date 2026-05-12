package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.auth.ObtenerUsuarioUseCase
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import com.example.vita.domain.usecase.retos.ActualizarProgresoRetoUseCase
import com.example.vita.domain.usecase.retos.GenerarYGuardarRetosUseCase
import com.example.vita.domain.usecase.retos.ObtenerRetosDeHoyUseCase
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
    private val obtenerRetosDeHoyUseCase: ObtenerRetosDeHoyUseCase,   // ✅ nuevo
    private val obtenerUsuarioUseCase: ObtenerUsuarioUseCase,
    private val generarYGuardarRetosUseCase: GenerarYGuardarRetosUseCase,
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init { cargarRetos() }

    fun cargarRetos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch

                // 1. Expira vencidos y carga los de hoy (todos los estados)
                val retosHoy = obtenerRetosDeHoyUseCase(uid)

                if (retosHoy.isNotEmpty()) {
                    _uiState.update { it.copy(retos = retosHoy, isLoading = false) }
                    return@launch
                }

                // 2. No hay retos de hoy → generar con IA
                _uiState.update { it.copy(mensajeCarga = "Diseñando tus retos con IA...") }

                val user    = obtenerUsuarioUseCase(uid)
                val nombre  = user?.name ?: "Entrenador"
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
            val uid           = authRepository.getCurrentUserId() ?: return@launch
            val nuevoProgreso = reto.currentValue + 1
            val completado    = nuevoProgreso >= reto.targetValue

            actualizarProgresoRetoUseCase(
                reto.copy(
                    currentValue = nuevoProgreso.coerceAtMost(reto.targetValue),
                    status       = if (completado) "COMPLETED" else "PROGRESSO"
                )
            )
            if (completado) agregarXpUseCase(uid, 80)
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