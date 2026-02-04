package com.example.vita.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProgresoRepository // Importante
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val progresoRepository: ProgresoRepository,
    private val agregarXpUseCase: AgregarXpUseCase // Mantenemos el UseCase porque ayuda con la lógica de nivel
) : ViewModel() {

    // 1. Un solo State para toda la pantalla (Igual que en Perfil)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId = authRepository.getCurrentUserId()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 2. Cargamos datos de la misma forma que el perfil
            val user = userRepository.getUserById(uid)
            var progress = progresoRepository.getProgreso(uid)

            // 3. Si progreso está vacío (como vimos en el inspector), lo creamos aquí mismo
            if (progress == null) {
                val nuevoProgreso = Progress(
                    id = 0,
                    userId = uid,
                    level = 1,
                    xp = 0,
                    streakDays = 1,
                    bmi = 0f,
                    weight = 0f,
                    date = System.currentTimeMillis()
                )
                progresoRepository.insertarProgreso(nuevoProgreso)
                progress = nuevoProgreso // Lo asignamos para mostrarlo
            }

            _uiState.update { it.copy(
                user = user,
                progress = progress,
                isLoading = false
            )}
        }
    }

    fun ganarExperiencia(tipo: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            val puntos = when (tipo.uppercase()) {
                "GODOT" -> 170
                "DIARIO" -> 80
                else -> 0
            }

            if (puntos > 0) {
                // 4. Usamos el UseCase para actualizar XP y Nivel
                agregarXpUseCase(uid, puntos)

                // 5. IMPORTANTE: Refrescamos los datos después de ganar XP
                // para que la CardInf se actualice (Igual que el perfilBiometrico)
                cargarDatos()
            }
        }
    }
}

// 6. El State para la Home
data class HomeUiState(
    val user: User? = null,
    val progress: Progress? = null,
    val isLoading: Boolean = true
)