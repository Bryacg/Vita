package com.example.vita.ui.screens.Game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.juegos.ProcesarResultadoJuegoUseCase
import com.example.vita.domain.model.GameResult

@HiltViewModel
class GameViewModel @Inject constructor(
    private val procesarResultadoJuegoUseCase: ProcesarResultadoJuegoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun procesarResultado(userId: String, juego: String, xp: Int) {
        viewModelScope.launch {
            val result = procesarResultadoJuegoUseCase(userId, juego, xp)
            _uiState.update { it.copy(lastResult = result) }
        }
    }
}

data class GameUiState(val lastResult: GameResult? = null)