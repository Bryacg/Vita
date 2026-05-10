package com.example.vita.ui.screens.Progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AchievementRepository
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.MealRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
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
    private val mealRepository: MealRepository,
    private val achievementRepository: AchievementRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            observarDatosReactivos(uid)
            cargarDatosEstaticos(uid)
        } else {
            // Sin sesión → todo en 0, sin carga
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Combina el Stream del usuario (XP, nivel) con el Stream de progreso (racha).
     * Room emite automáticamente cuando AgregarXpUseCase actualiza la DB.
     */
    private fun observarDatosReactivos(uid: String) {
        viewModelScope.launch {
            combine(
                userRepository.getUserStream(uid),
                progresoRepository.getProgresoStream(uid)
            ) { user, progreso ->
                Pair(user, progreso)
            }.collectLatest { (user, progreso) ->
                _uiState.update { current ->
                    current.copy(
                        nivelActual = user?.currentLevel ?: 0,
                        xpTotal = user?.currentXp ?: 0,
                        rachaActual = progreso?.streakDays ?: 0,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Carga datos que no son reactivos: comidas, logros, IMC, gráfico semanal.
     * Si no hay datos → queda en 0 por defecto desde ProgressUiState.
     */
    private fun cargarDatosEstaticos(uid: String) {
        viewModelScope.launch {
            try {
                val comidas = mealRepository.getMealsByUser(uid)
                val logros = achievementRepository.getAchievementsByUser(uid)
                val perfil = profileRepository.getProfileByUserId(uid)
                val semana = progresoRepository.getProgresoUltimaSemana(uid)

                // Calcula IMC si hay datos biométricos válidos
                val imc = if (perfil != null && perfil.weight > 0f && perfil.height > 0f) {
                    val alturaMetros = perfil.height / 100f
                    perfil.weight / (alturaMetros * alturaMetros)
                } else 0f

                _uiState.update {
                    it.copy(
                        totalComidas = comidas.size,
                        logrosDesbloqueados = logros.count { logro -> logro.unlocked },
                        totalLogros = logros.size.takeIf { s -> s > 0 } ?: 4,
                        imc = imc,
                        progresoDeSemana = semana
                    )
                }
            } catch (e: Exception) {
                // Error silencioso: los datos quedan en 0, no rompemos la pantalla
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // Llamado al hacer pull-to-refresh
    fun refrescar() {
        val uid = authRepository.getCurrentUserId() ?: return
        cargarDatosEstaticos(uid)
    }
}

data class ProgressUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    // Datos reactivos (se actualizan automáticamente con cada XP ganada)
    val nivelActual: Int = 0,
    val xpTotal: Int = 0,
    val rachaActual: Int = 0,
    // Datos estáticos (carga inicial + refresh manual)
    val totalComidas: Int = 0,
    val logrosDesbloqueados: Int = 0,
    val totalLogros: Int = 4,
    val imc: Float = 0f,
    val progresoDeSemana: List<Progress> = emptyList()
)