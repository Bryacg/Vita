package com.example.vita.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.GameConfig
import com.example.vita.domain.model.Meal
import com.example.vita.domain.model.NutritionResult
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.comida.EliminarComidaUseCase
import com.example.vita.domain.usecase.comida.ObtenerComidasHoyUseCase
import com.example.vita.domain.usecase.comida.RegistrarComidaConXpUseCase
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import com.example.vita.domain.usecase.progreso.ObtenerOCrearProgresoUseCase
import com.example.vita.domain.usecase.retos.ActualizarProgresoRetoUseCase
import com.example.vita.domain.usecase.retos.ObtenerRetosActivosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val progresoRepository: ProgresoRepository,
    private val obtenerOCrearProgresoUseCase: ObtenerOCrearProgresoUseCase,
    private val obtenerRetosActivosUseCase: ObtenerRetosActivosUseCase,
    private val obtenerComidasHoyUseCase: ObtenerComidasHoyUseCase,
    private val registrarComidaConXpUseCase: RegistrarComidaConXpUseCase,
    private val eliminarComidaUseCase: EliminarComidaUseCase,
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId get() = authRepository.getCurrentUserId()

    init {
        val uid = userId
        if (uid != null) {
            viewModelScope.launch { obtenerOCrearProgresoUseCase(uid) }
            observarUsuarioYProgresoReactivo(uid)
        }
        cargarDatosEstaticos(mostrarCarga = true)
    }

    /**
     * Observa user y progress como Flow (mismo patrón que ProgressViewModel).
     * Así, cualquier cambio de XP/nivel/racha —venga de Retos, Comida o el
     * minijuego de Godot— se refleja automáticamente en Home sin necesidad
     * de recargar la pantalla ni reabrir la app.
     */
    private fun observarUsuarioYProgresoReactivo(uid: String) {
        viewModelScope.launch {
            combine(
                userRepository.getUserStream(uid),
                progresoRepository.getProgresoStream(uid)
            ) { user, progress -> Pair(user, progress) }
                .collectLatest { (user, progress) ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            progress = progress ?: it.progress,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Carga los datos que aún no son reactivos por Flow (comidas de hoy,
     * retos activos). Se puede volver a llamar manualmente tras acciones
     * puntuales (registrar comida, actualizar reto).
     */
    fun cargarDatosEstaticos(mostrarCarga: Boolean = false) {
        val uid = userId ?: return
        viewModelScope.launch {
            if (mostrarCarga) _uiState.update { it.copy(isLoading = true) }
            try {
                val comidasHoy    = obtenerComidasHoyUseCase(uid)
                val totalKcal     = comidasHoy.sumOf { it.calories }
                val promedioSalud = if (comidasHoy.isNotEmpty())
                    comidasHoy.map { it.healthyScore }.average().toInt() else 0

                val todosLosRetos   = obtenerRetosActivosUseCase(uid)
                val retoPrioritario = todosLosRetos
                    .find { it.currentValue > 0 && it.status != "COMPLETED" }
                    ?: todosLosRetos.find { it.status != "COMPLETED" }

                _uiState.update {
                    it.copy(
                        retoDestacado       = retoPrioritario,
                        comidasHoy          = comidasHoy,
                        totalCaloriesHoy    = totalKcal,
                        saludNutricionalHoy = promedioSalud,
                        isLoading           = false,
                        error               = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Se conserva por compatibilidad con las llamadas existentes en el resto del ViewModel.
    fun cargarDatos(mostrarCarga: Boolean = true) {
        cargarDatosEstaticos(mostrarCarga)
    }

    fun registrarNuevaComida(nombre: String, kcal: Int) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val resultado = registrarComidaConXpUseCase(uid, nombre, kcal)
                _uiState.update { it.copy(ultimaNutricion = resultado) }
                cargarDatosEstaticos(mostrarCarga = false)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun limpiarFeedbackNutricion() {
        _uiState.update { it.copy(ultimaNutricion = null) }
    }

    fun eliminarComida(mealId: Long) {
        viewModelScope.launch {
            try {
                eliminarComidaUseCase(mealId)
                cargarDatosEstaticos(mostrarCarga = false)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun ganarExperienciaMinijuego() {
        val uid = userId ?: return
        viewModelScope.launch {
            agregarXpUseCase(uid, GameConfig.XP_MINIJUEGO_GODOT)
            cargarDatosEstaticos(mostrarCarga = false)
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            val nuevoProgreso  = (reto.currentValue + 1).coerceAtMost(reto.targetValue)
            val estaCompletado = nuevoProgreso >= reto.targetValue
            actualizarProgresoRetoUseCase(reto.copy(
                currentValue = nuevoProgreso,
                status       = if (estaCompletado) "COMPLETED" else "PROGRESSO"
            ))
            if (estaCompletado) agregarXpUseCase(uid, GameConfig.XP_RETO_DIARIO)
            cargarDatosEstaticos(mostrarCarga = false)
        }
    }

    fun completarRetoInstantaneo(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            actualizarProgresoRetoUseCase(
                reto.copy(currentValue = reto.targetValue, status = "COMPLETED")
            )
            agregarXpUseCase(uid, GameConfig.XP_RETO_DIARIO)
            cargarDatosEstaticos(mostrarCarga = false)
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val progress: Progress? = null,
    val retoDestacado: Challenger? = null,
    val comidasHoy: List<Meal> = emptyList(),
    val totalCaloriesHoy: Int = 0,
    val saludNutricionalHoy: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val ultimaNutricion: NutritionResult? = null
)