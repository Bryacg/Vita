package com.example.vita.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.model.GameConfig
import com.example.vita.domain.model.Meal
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.auth.ObtenerUsuarioUseCase
import com.example.vita.domain.usecase.comida.EliminarComidaUseCase
import com.example.vita.domain.usecase.comida.ObtenerComidasHoyUseCase
import com.example.vita.domain.usecase.comida.RegistrarComidaUseCase
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
    private val obtenerUsuarioUseCase: ObtenerUsuarioUseCase,
    private val obtenerOCrearProgresoUseCase: ObtenerOCrearProgresoUseCase,
    private val obtenerRetosActivosUseCase: ObtenerRetosActivosUseCase,
    private val obtenerComidasHoyUseCase: ObtenerComidasHoyUseCase,
    private val registrarComidaUseCase: RegistrarComidaUseCase,
    private val eliminarComidaUseCase: EliminarComidaUseCase,       // nuevo
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId get() = authRepository.getCurrentUserId()

    init { cargarDatos() }

    // mostrarCarga = false evita el flash de CircularProgressIndicator
    // cuando solo se refresca tras agregar/eliminar una comida
    fun cargarDatos(mostrarCarga: Boolean = true) {
        val uid = userId ?: return
        viewModelScope.launch {
            if (mostrarCarga) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val user         = obtenerUsuarioUseCase(uid)
                val progress     = obtenerOCrearProgresoUseCase(uid)
                val comidasHoy   = obtenerComidasHoyUseCase(uid)
                val totalKcal    = comidasHoy.sumOf { it.calories }
                val promedioSalud = if (comidasHoy.isNotEmpty())
                    comidasHoy.map { it.healthyScore }.average().toInt()
                else 0

                val todosLosRetos   = obtenerRetosActivosUseCase(uid)
                val retoPrioritario = todosLosRetos
                    .find { it.currentValue > 0 && it.status != "COMPLETED" }
                    ?: todosLosRetos.find { it.status != "COMPLETED" }

                _uiState.update {
                    it.copy(
                        user                = user,
                        progress            = progress,
                        retoDestacado       = retoPrioritario,
                        comidasHoy          = comidasHoy,       // expone la lista
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

    fun registrarNuevaComida(nombre: String, kcal: Int, salud: Int) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                registrarComidaUseCase(
                    Meal(
                        id           = 0,
                        userId       = uid,
                        name         = nombre,
                        calories     = kcal,
                        healthyScore = salud,
                        date         = System.currentTimeMillis()
                    )
                )
                // Sin loading flicker — la lista se actualiza silenciosamente
                cargarDatos(mostrarCarga = false)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun eliminarComida(mealId: Long) {
        viewModelScope.launch {
            try {
                eliminarComidaUseCase(mealId)
                cargarDatos(mostrarCarga = false)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun ganarExperienciaMinijuego() {
        val uid = userId ?: return
        viewModelScope.launch {
            agregarXpUseCase(uid, GameConfig.XP_MINIJUEGO_GODOT)
            cargarDatos(mostrarCarga = false)
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            val nuevoProgreso  = (reto.currentValue + 1).coerceAtMost(reto.targetValue)
            val estaCompletado = nuevoProgreso >= reto.targetValue
            actualizarProgresoRetoUseCase(
                reto.copy(
                    currentValue = nuevoProgreso,
                    status       = if (estaCompletado) "COMPLETED" else "PROGRESSO"
                )
            )
            if (estaCompletado) agregarXpUseCase(uid, GameConfig.XP_RETO_DIARIO)
            cargarDatos(mostrarCarga = false)
        }
    }

    fun completarRetoInstantaneo(reto: Challenger) {
        val uid = userId ?: return
        viewModelScope.launch {
            actualizarProgresoRetoUseCase(
                reto.copy(currentValue = reto.targetValue, status = "COMPLETED")
            )
            agregarXpUseCase(uid, GameConfig.XP_RETO_DIARIO)
            cargarDatos(mostrarCarga = false)
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val progress: Progress? = null,
    val retoDestacado: Challenger? = null,
    val comidasHoy: List<Meal> = emptyList(),   // ← campo nuevo
    val totalCaloriesHoy: Int = 0,
    val saludNutricionalHoy: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
