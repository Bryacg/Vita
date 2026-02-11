package com.example.vita.ui.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.*
import com.example.vita.domain.repository.*
import com.example.vita.domain.usecase.auth.SignOutUseCase
import com.example.vita.domain.usecase.perfil.ManageReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Definimos las "Reglas de Juego" para los logros
object AchievementConstants {
    val LISTA_LOGROS_BASE = listOf(
        // Nombre, Descripción, Requisito (valor a comparar)
        Triple("Nivel Principiante", "Alcanza el nivel 2", 2),
        Triple("Maestro Gourmet", "Agrega 3 o más preferencias alimentarias", 3),
        Triple("Perfil Completo", "Registra tus datos biométricos", 1), // 1 si el perfil no es null
        Triple("Racha de Agua", "Activa los recordatorios de hidratación", 1)
    )
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val authRepository: AuthRepository,
    private val achievementRepository: AchievementRepository,
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

                // Carga en paralelo
                val userDef = async { userRepository.getUserById(uid) }
                val profileDef = async { profileRepository.getProfileByUserId(uid) }
                val prefDef = async { foodRepository.getUserFoodPreferences(uid) }

                val user = userDef.await()
                val profile = profileDef.await()
                val preferences = prefDef.await()

                // Recordatorios
                val (aguaActivo, aguaHora) = manageReminderUseCase.obtenerEstadoGuardado("agua")
                val (caminarActivo, caminarHora) = manageReminderUseCase.obtenerEstadoGuardado("caminar")

                // 2. LÓGICA DE COMPARACIÓN DINÁMICA
                // Generamos la lista de logros comparando los datos actuales
                val logrosCalculados = AchievementConstants.LISTA_LOGROS_BASE.mapIndexed { index, (nombre, desc, meta) ->
                    val estaDesbloqueado = when (nombre) {
                        "Nivel Principiante" -> (user?.currentLevel?: 1) >= meta
                        "Maestro Gourmet" -> preferences.size >= meta
                        "Perfil Completo" -> profile != null
                        "Racha de Agua" -> aguaActivo
                        else -> false
                    }

                    Achievement(
                        id = index.toLong(),
                        userId = uid,
                        name = nombre,
                        description = desc,
                        unlocked = estaDesbloqueado
                    )
                }

                _uiState.update { current ->
                    current.copy(
                        user = user,
                        profile = profile,
                        foodPreferences = preferences,
                        logros = logrosCalculados, // <--- Lista calculada al vuelo
                        isLoading = false,
                        aguaRecordatorioActivo = aguaActivo,
                        aguaHora = aguaHora,
                        caminarRecordatorioActivo = caminarActivo,
                        caminarHora = caminarHora
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    // --- MÉTODOS DE ACTUALIZACIÓN (Llaman a cargarDatos para refrescar logros) ---

    fun actualizarRecordatorio(tipo: String, activo: Boolean, horaStr: String) {
        viewModelScope.launch {
            try {
                val partes = horaStr.split(":")
                if (partes.size == 2) {
                    manageReminderUseCase(tipo, activo, partes[0].toInt(), partes[1].toInt())
                    cargarDatos() // Refrescamos para ver si desbloqueó "Racha de Agua"
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun agregarPreferenciaAlimentaria(foodName: String, type: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val food = foodRepository.getFoodByName(foodName)
            val foodId = food?.id ?: foodRepository.saveFood(Food(0, foodName, "General"))
            foodRepository.savePreference(FoodPreference(0, uid, foodId, type))
            cargarDatos() // Refrescamos para ver si desbloqueó "Maestro Gourmet"
        }
    }

    fun guardarDatosFisicos(peso: Float, altura: Float, edad: Int, genero: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val currentId = _uiState.value.profile?.id ?: 0
            profileRepository.saveProfile(Profile(currentId, uid, altura, peso, edad, genero))
            cargarDatos() // Refrescamos para ver si desbloqueó "Perfil Completo"
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