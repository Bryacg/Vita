package com.example.vita.ui.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.*
import com.example.vita.domain.repository.*
import com.example.vita.domain.usecase.achievement.EvaluarLogrosUseCase
import com.example.vita.domain.usecase.auth.SignOutUseCase
import com.example.vita.domain.usecase.perfil.ManageReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ AchievementConstants eliminado del ViewModel — vive en AchievementRules (domain)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val authRepository: AuthRepository,
    // ✅ achievementRepository eliminado — los logros se calculan sin BD
    private val evaluarLogrosUseCase: EvaluarLogrosUseCase, // ✅ UseCase nuevo
    private val signOutUseCase: SignOutUseCase,
    private val manageReminderUseCase: ManageReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch

                val userDef       = async { userRepository.getUserById(uid) }
                val profileDef    = async { profileRepository.getProfileByUserId(uid) }
                val prefDef       = async { foodRepository.getUserFoodPreferences(uid) }

                val user        = userDef.await()
                val profile     = profileDef.await()
                val preferences = prefDef.await()

                val (aguaActivo, aguaHora)     = manageReminderUseCase.obtenerEstadoGuardado("agua")
                val (caminarActivo, caminarHora) = manageReminderUseCase.obtenerEstadoGuardado("caminar")

                // ✅ Evaluación de logros delegada al UseCase del dominio
                val logrosCalculados = evaluarLogrosUseCase(
                    uid                   = uid,
                    nivelActual           = user?.currentLevel ?: 1,
                    cantidadPreferencias  = preferences.size,
                    tienePerfilCompleto   = profile != null && profile.weight > 0f,
                    aguaActiva            = aguaActivo
                )

                _uiState.update { current ->
                    current.copy(
                        user                    = user,
                        profile                 = profile,
                        foodPreferences         = preferences,
                        logros                  = logrosCalculados,
                        isLoading               = false,
                        aguaRecordatorioActivo  = aguaActivo,
                        aguaHora                = aguaHora,
                        caminarRecordatorioActivo = caminarActivo,
                        caminarHora             = caminarHora
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    fun actualizarRecordatorio(tipo: String, activo: Boolean, horaStr: String) {
        viewModelScope.launch {
            try {
                val partes = horaStr.split(":")
                if (partes.size == 2) {
                    manageReminderUseCase(tipo, activo, partes[0].toInt(), partes[1].toInt())
                    cargarDatos()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun agregarPreferenciaAlimentaria(foodName: String, type: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val food   = foodRepository.getFoodByName(foodName)
            val foodId = food?.id ?: foodRepository.saveFood(Food(0, foodName, "General"))
            foodRepository.savePreference(FoodPreference(0, uid, foodId, type))
            cargarDatos()
        }
    }

    fun guardarDatosFisicos(peso: Float, altura: Float, edad: Int, genero: String) {
        viewModelScope.launch {
            val uid       = authRepository.getCurrentUserId() ?: return@launch
            val currentId = _uiState.value.profile?.id ?: 0
            profileRepository.saveProfile(Profile(currentId, uid, altura, peso, edad, genero))
            cargarDatos()
        }
    }

    fun eliminarPreferenciaAlimentaria(preference: FoodPreference) {
        viewModelScope.launch {
            foodRepository.deletePreference(preference)
            cargarDatos()
        }
    }

    fun cerrarSesion(onNavigateToLogin: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            onNavigateToLogin()
        }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val profile: Profile? = null,
    val foodPreferences: List<Pair<Food, FoodPreference>> = emptyList(),
    val logros: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
    val aguaRecordatorioActivo: Boolean = false,
    val aguaHora: String = "09:00",
    val caminarRecordatorioActivo: Boolean = false,
    val caminarHora: String = "17:30"
)