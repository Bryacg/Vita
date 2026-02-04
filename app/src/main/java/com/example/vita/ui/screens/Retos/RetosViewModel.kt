package com.example.vita.ui.screens.Retos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.retos.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RetosViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: com.example.vita.domain.repository.ChallengeRepository, // Inyectamos el repo
    private val actualizarProgresoRetoUseCase: ActualizarProgresoRetoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        // Reto de prueba inicial para no ver la pantalla vacía
        _uiState.update { it.copy(isLoading = true) }
        cargarRetos()
    }

    fun cargarRetos() {
        viewModelScope.launch {
            try {
                val uid = authRepository.getCurrentUserId() ?: return@launch
                val retosDB = repository.getActiveChallenges(uid)

                // USAR .map { it.copy() } garantiza que cada objeto sea nuevo
                // USAR .toList() garantiza que la lista sea una nueva instancia
                val listaNueva = retosDB.map { it.copy() }.toList()

                _uiState.update { it.copy(
                    retos = listaNueva,
                    isLoading = false
                ) }
                android.util.Log.d("VITA_LOG", "Lista refrescada: ${listaNueva.size} ítems")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun actualizarProgresoReto(reto: Challenger) {
        viewModelScope.launch {
            // 1. Calculamos el nuevo valor de progreso
            val nuevoProgreso = reto.currentValue + 1

            // 2. Determinamos si el reto ya se completó
            val nuevoEstado = if (nuevoProgreso >= reto.targetValue) "COMPLETED" else "PROGRESSO"

            // 3. Creamos una copia del objeto con los datos actualizados
            val retoActualizado = reto.copy(
                currentValue = nuevoProgreso.coerceAtMost(reto.targetValue),
                status = nuevoEstado
            )

            // 4. Usamos el UseCase para guardar en la base de datos
            // Asegúrate de tener inyectado ActualizarProgresoRetoUseCase
            actualizarProgresoRetoUseCase(retoActualizado)

            // 5. Refrescamos la lista local para que la UI se actualice
            cargarRetos()
        }
    }
    fun completarRetoInstantaneo(reto: Challenger) {
        viewModelScope.launch {
            // Creamos la copia con el valor máximo y estado completado
            val retoCompletado = reto.copy(
                currentValue = reto.targetValue,
                status = "COMPLETED"
            )

            // Persistimos en la base de datos
            actualizarProgresoRetoUseCase(retoCompletado)

            // Refrescamos la UI
            cargarRetos()
        }
    }
}

data class RetosUiState(
    val retos: List<Challenger> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)