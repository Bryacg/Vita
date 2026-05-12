package com.example.vita.ui.screens.Progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.FoodRepository
import com.example.vita.domain.repository.MealRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.achievement.EvaluarLogrosUseCase
import com.example.vita.domain.usecase.perfil.ManageReminderUseCase
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
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val evaluarLogrosUseCase: EvaluarLogrosUseCase,
    private val manageReminderUseCase: ManageReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            observarDatosReactivos(uid)
            cargarDatosEstaticos(uid)
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
                    _uiState.update { current ->
                        current.copy(
                            nivelActual = user?.currentLevel ?: 0,
                            xpTotal     = user?.currentXp ?: 0,
                            rachaActual = progreso?.streakDays ?: 0,
                            isLoading   = false
                        )
                    }
                    cargarDatosEstaticos(uid)
                }
        }
    }

    fun cargarDatosEstaticos(uid: String? = authRepository.getCurrentUserId()) {
        val userId = uid ?: return
        viewModelScope.launch {
            try {
                val user         = userRepository.getUserById(userId)
                val comidas      = mealRepository.getMealsByUser(userId)
                val perfil       = profileRepository.getProfileByUserId(userId)
                val semana       = progresoRepository.getProgresoUltimaSemana(userId)
                val preferencias = foodRepository.getUserFoodPreferences(userId)
                val (aguaActivo, _) = manageReminderUseCase.obtenerEstadoGuardado("agua")

                val logros = evaluarLogrosUseCase(
                    uid                  = userId,
                    nivelActual          = user?.currentLevel ?: 0,
                    cantidadPreferencias = preferencias.size,
                    tienePerfilCompleto  = perfil != null && perfil.weight > 0f,
                    aguaActiva           = aguaActivo
                )

                val imc = if (perfil != null && perfil.weight > 0f && perfil.height > 0f) {
                    val alturaMetros = perfil.height / 100f
                    perfil.weight / (alturaMetros * alturaMetros)
                } else 0f

                _uiState.update {
                    it.copy(
                        totalComidas        = comidas.size,
                        logrosDesbloqueados = logros.count { logro -> logro.unlocked },
                        totalLogros         = logros.size,
                        imc                 = imc,
                        progresoDeSemana    = semana
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refrescar() {
        cargarDatosEstaticos()
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