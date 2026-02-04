package com.example.vita.ui.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.FoodRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.auth.SignOutUseCase
import com.example.vita.domain.usecase.perfil.ManageReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val authRepository: AuthRepository,
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
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val user = userRepository.getUserById(uid)
            val profile = profileRepository.getProfileByUserId(uid)
            val preferences = foodRepository.getUserFoodPreferences(uid)

            // LEER LOS ESTADOS GUARDADOS DEL DISCO
            val (aguaActivo, aguaHora) = manageReminderUseCase.obtenerEstadoGuardado("agua")
            val (caminarActivo, caminarHora) = manageReminderUseCase.obtenerEstadoGuardado("caminar")

            _uiState.update { current ->
                current.copy(
                    user = user,
                    profile = profile,
                    foodPreferences = preferences,
                    isLoading = false,
                    // Aplicamos los valores recuperados
                    aguaRecordatorioActivo = aguaActivo,
                    aguaHora = aguaHora,
                    caminarRecordatorioActivo = caminarActivo,
                    caminarHora = caminarHora
                )
            }
        }
    }

    // --- LÓGICA DE RECORDATORIOS ---

    fun actualizarRecordatorio(tipo: String, activo: Boolean, horaStr: String) {
        // 1. Actualizamos el estado de la UI (Memoria RAM)
        _uiState.update { current ->
            if (tipo.lowercase() == "agua") {
                current.copy(aguaRecordatorioActivo = activo, aguaHora = horaStr)
            } else {
                current.copy(caminarRecordatorioActivo = activo, caminarHora = horaStr)
            }
        }

        // 2. Programamos la alarma en el sistema a través del UseCase
        try {
            val partes = horaStr.split(":")
            if (partes.size == 2) {
                val h = partes[0].toInt()
                val m = partes[1].toInt()
                manageReminderUseCase(tipo, activo, h, m)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- MÉTODOS DE DATOS ---

    fun agregarPreferenciaAlimentaria(foodName: String, type: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val food = foodRepository.getFoodByName(foodName)
            val foodId = food?.id ?: foodRepository.saveFood(Food(id = 0, name = foodName, category = "General"))

            val newPreference = FoodPreference(
                id = 0,
                userId = uid,
                foodId = foodId,
                preferenceType = type
            )
            foodRepository.savePreference(newPreference)
            cargarDatos()
        }
    }

    fun eliminarPreferenciaAlimentaria(preference: FoodPreference) {
        viewModelScope.launch {
            foodRepository.deletePreference(preference)
            cargarDatos()
        }
    }

    fun guardarDatosFisicos(peso: Float, altura: Float, edad: Int, genero: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val currentId = _uiState.value.profile?.id ?: 0
            val perfil = Profile(
                id = currentId,
                userId = uid,
                height = altura,
                weight = peso,
                age = edad,
                gender = genero
            )
            profileRepository.saveProfile(perfil)
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
    val isLoading: Boolean = true,
    // Estados de misiones
    val aguaRecordatorioActivo: Boolean = false,
    val aguaHora: String = "09:00",
    val caminarRecordatorioActivo: Boolean = false,
    val caminarHora: String = "17:30"
)