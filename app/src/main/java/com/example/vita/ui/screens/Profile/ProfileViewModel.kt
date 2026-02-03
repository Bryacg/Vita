package com.example.vita.ui.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.model.Profile
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.FoodRepository // Importante
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.auth.SignOutUseCase
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
    private val foodRepository: FoodRepository, // Inyectamos FoodRepository
    private val authRepository: AuthRepository,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            // Cargamos todo en paralelo para mejor rendimiento
            val user = userRepository.getUserById(uid)
            val profile = profileRepository.getProfileByUserId(uid)
            val preferences = foodRepository.getUserFoodPreferences(uid) // Usamos foodRepository

            _uiState.update { it.copy(
                user = user,
                profile = profile,
                foodPreferences = preferences,
                isLoading = false
            )}
        }
    }

    fun agregarPreferenciaAlimentaria(foodName: String, type: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            // 1. Buscamos si el alimento ya existe por nombre
            var food = foodRepository.getFoodByName(foodName)

            // 2. Si no existe, lo creamos para obtener un ID
            val foodId = if (food == null) {
                foodRepository.saveFood(Food(id = 0, name = foodName, category = "General"))
            } else {
                food.id
            }

            // 3. Creamos la preferencia vinculada al usuario y al alimento
            val newPreference = FoodPreference(
                id = 0, // Room generará el ID
                userId = uid,
                foodId = foodId,
                preferenceType = type
            )

            foodRepository.savePreference(newPreference)
            cargarDatos() // Refrescamos la UI
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

            // Mantenemos el ID si ya existe un perfil para actualizar en lugar de crear duplicados
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
    val isLoading: Boolean = true
)