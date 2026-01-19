package com.example.vita.ui.screens.Game


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.GameResult
import com.example.vita.domain.usecase.juegos.ProcesarResultadoJuegoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val procesarResultadoJuegoUseCase: ProcesarResultadoJuegoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun procesarResultado(userId: String, nombreJuego: String, xpGanada: Int) {
        viewModelScope.launch {
            // Creamos el objeto que el UseCase espera
            val resultado = GameResult(
                userId = userId,
                name = nombreJuego,  // <--- Cambiado de gameName a name
                weight = 1,          // Asegúrate de incluir este campo que definimos antes
                xpEarned = xpGanada,
                date = System.currentTimeMillis()
            )

            // Ahora pasamos el objeto 'resultado' (GameResult) en lugar de los strings sueltos
            val result = procesarResultadoJuegoUseCase(resultado)

            _uiState.update { it.copy(lastResult = result) }
        }
    }
}

data class GameUiState(val lastResult: GameResult? = null)