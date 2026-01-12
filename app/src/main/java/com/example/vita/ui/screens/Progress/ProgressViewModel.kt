package com.example.vita.ui.screens.Progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.progreso.*

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val agregarXpUseCase: AgregarXpUseCase,
    private val actualizarNivelUseCase: ActualizarNivelUseCase,
    private val trackRachaUseCase: TrackRachaUseCase,
    private val resetearRachaUseCase: ResetearRachaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun agregarXp(userId: String, xp: Int) {
        viewModelScope.launch {
            agregarXpUseCase(userId, xp)
            _uiState.update { it.copy(xp = _uiState.value.xp + xp) }
        }
    }

    fun actualizarNivel(userId: String) {
        viewModelScope.launch {
            // Calculas el nivel en base al XP actual
            val nuevoNivel = calcularNivel(_uiState.value.xp)

            // Persistes el nivel en el repositorio usando el UseCase
            val nivel = actualizarNivelUseCase(userId, nuevoNivel)

            // Actualizas el estado de la UI
            _uiState.update { it.copy(level = nivel) }
        }
    }

    fun trackRacha(userId: String, actividadHoy: Boolean) {
        viewModelScope.launch {
            val streak = trackRachaUseCase(userId, actividadHoy)
            _uiState.update { it.copy(streakDays = streak) }
        }
    }

    private fun calcularNivel(xp: Int): Int {
        // Ejemplo simple: cada 100 XP sube un nivel
        return (xp / 100) + 1
    }
}

data class ProgressUiState(
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0
)