package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.retos.InsertarRetoUseCase
import com.example.vita.domain.usecase.retos.ObtenerRetosActivosUseCase
import com.example.vita.domain.usecase.retos.ActualizarProgresoRetoUseCase
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository

@HiltViewModel
class RetosViewModel @Inject constructor(
    private val insertarRetoUseCase: InsertarRetoUseCase,
    private val obtenerRetosActivosUseCase: ObtenerRetosActivosUseCase,
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val authRepository: AuthRepository //
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    fun cargarRetos() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId()
            if (uid != null) {
                // Ahora sí pasamos el uid al UseCase
                val retos = obtenerRetosActivosUseCase(uid)
                _uiState.update { it.copy(retos = retos) }
            } else {
                // Manejar caso de usuario no logueado si es necesario
                _uiState.update { it.copy(retos = emptyList()) }
            }
        }

        fun insertarReto(reto: Challenger) {
            viewModelScope.launch {
                insertarRetoUseCase(reto)
                cargarRetos()
            }
        }

        fun actualizarProgreso(retoId: Long, progreso: Int) {
            viewModelScope.launch {
                actualizarProgresoRetoUseCase(retoId, progreso)
                cargarRetos()
            }
        }
    }
}
data class RetosUiState(val retos: List<Challenger> = emptyList())