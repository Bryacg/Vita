package com.example.vita.ui.screens.Game

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.data.remote.godot.GameResultBuffer
import com.example.vita.domain.usecase.godot.ProcesarResultadoJuegoUseCase
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

    fun abrirJuego(context: Context) {
        val packageName = "com.example.atrapasalud"
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)

        if (intent != null) {
            GameResultBuffer.ultimoResultado = null // Limpiar antes de empezar
            context.startActivity(intent)
        } else {
            Log.e("Vita", "El juego no está instalado")
            _uiState.update { it.copy(mensajeResultado = "Error: El juego no está instalado") }
        }
    }

    fun verificarResultadoTrasRegresar() {
        viewModelScope.launch {
            val resultadoString = GameResultBuffer.ultimoResultado

            if (resultadoString != null) {
                // Llamamos al UseCase pasando el String
                procesarResultadoJuegoUseCase(resultadoString)

                // Actualizamos la UI
                _uiState.update {
                    it.copy(mensajeResultado = "¡Procesado: $resultadoString!")
                }

                // Importante: Limpiar el buffer
                GameResultBuffer.ultimoResultado = null
            }
        }
    }
}

// Estado de la UI simplificado
data class GameUiState(
    val mensajeResultado: String? = null,
    val isLoading: Boolean = false
)