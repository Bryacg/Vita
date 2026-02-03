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
    private val repository: com.example.vita.domain.repository.ChallengeRepository // Inyectamos el repo
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

                // 1. Primero verificamos si YA existen retos en la Base de Datos
                val retosExistentes = repository.getActiveChallenges(uid)

                if (retosExistentes.isEmpty()) {
                    // 2. SOLO si está vacía, llamamos a la IA
                    android.util.Log.d("VITA_LOG", "DB vacía, generando retos nuevos...")
                    repository.generarYGuardarRetos(uid, "Usuario")

                    // 3. Volvemos a consultar para obtener los nuevos
                    val nuevosRetos = repository.getActiveChallenges(uid)
                    _uiState.update { it.copy(retos = nuevosRetos, isLoading = false) }
                } else {
                    // 4. Si ya había retos, simplemente los mostramos
                    android.util.Log.d("VITA_LOG", "Retos encontrados, saltando generación IA")
                    _uiState.update { it.copy(retos = retosExistentes, isLoading = false) }
                }

            } catch (e: Exception) {
                android.util.Log.e("VITA_LOG", "Error: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun actualizarProgreso(retoId: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            repository.updateProgress(retoId, nuevoProgreso)
            // Después de actualizar, recargamos para ver los cambios
            val uid = authRepository.getCurrentUserId() ?: return@launch
            val actualizados = repository.getActiveChallenges(uid)
            _uiState.update { it.copy(retos = actualizados) }
        }
    }
}

data class RetosUiState(
    val retos: List<Challenger> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)