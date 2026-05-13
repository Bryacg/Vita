package com.example.vita.ui.screens.Progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.usecase.progreso.ObtenerResumenProgresoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val progresoRepository: ProgresoRepository,
    private val obtenerResumenProgresoUseCase: ObtenerResumenProgresoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            observarDatosReactivos(uid)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observarDatosReactivos(uid: String) {
        viewModelScope.launch {
            combine(
                userRepository.getUserStream(uid),
                progresoRepository.getProgresoStream(uid)
            ) { user, progreso -> Pair(user, progreso) }
                .collectLatest { (user, progreso) ->
                    val nivelActual = user?.currentLevel ?: 0
                    _uiState.update { it.copy(
                        nivelActual = nivelActual,
                        xpTotal     = user?.currentXp ?: 0,
                        rachaActual = progreso?.streakDays ?: 0,
                        isLoading   = false
                    )}
                    cargarResumen(uid, nivelActual)
                }
        }
    }

    private fun cargarResumen(uid: String, nivelActual: Int) {
        viewModelScope.launch {
            try {
                val resumen = obtenerResumenProgresoUseCase(uid, nivelActual)
                _uiState.update { it.copy(
                    totalComidas        = resumen.totalComidas,
                    logrosDesbloqueados = resumen.logros.count { l -> l.unlocked },
                    totalLogros         = resumen.logros.size,
                    imc                 = resumen.imc,
                    progresoDeSemana    = resumen.semana
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refrescar() {
        val uid = authRepository.getCurrentUserId() ?: return
        val nivelActual = _uiState.value.nivelActual
        cargarResumen(uid, nivelActual)
    }
}

data class ProgressUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val nivelActual: Int = 0,
    val xpTotal: Int = 0,
    val rachaActual: Int = 0,
    val totalComidas: Int = 0,
    val logrosDesbloqueados: Int = 0,
    val totalLogros: Int = 4,
    val imc: Float = 0f,
    val progresoDeSemana: List<Progress> = emptyList()
)