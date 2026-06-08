package com.example.vita.ui.screens.Game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.GameConfig
import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.usecase.juegos.ProcesarResultadoJuegoUseCase
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val procesarResultadoJuegoUseCase: ProcesarResultadoJuegoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Evento de un solo disparo para que la Screen lance el Intent
    private val _navegarAJuego = MutableSharedFlow<String>()
    val navegarAJuego: SharedFlow<String> = _navegarAJuego.asSharedFlow()

    // Registro del juego activo para identificarlo después
    private var juegoActual: String = ""

    fun solicitarAbrirJuego(packageName: String = "com.example.atrapasalud") {
        viewModelScope.launch {
            juegoActual = packageName  // Guardar qué juego se abrió
            _uiState.update { it.copy(juegoActivo = true, mensajeResultado = null) }
            _navegarAJuego.emit(packageName)
        }
    }

    // Llamado por la Screen cuando el usuario regresa del juego
    fun onRegresarDeJuego(resultado: String?) {
        val uid = authRepository.getCurrentUserId() ?: run {
            android.util.Log.e("GameViewModel", "❌ No hay usuario autenticado")
            return
        }

        android.util.Log.d("GameViewModel", "[1] Procesando resultado: '$resultado'")
        android.util.Log.d("GameViewModel", "[2] Usuario: $uid")
        android.util.Log.d("GameViewModel", "[3] Juego actual: $juegoActual")

        viewModelScope.launch {
            try {
                // Limpiar resultado después de procesarlo
                val resultadoFinal = resultado ?: run {
                    android.util.Log.w("GameViewModel", "⚠️ Resultado es NULL - no se procesará nada")
                    _uiState.update { it.copy(juegoActivo = false) }
                    return@launch
                }

                android.util.Log.d("GameViewModel", "[4] Resultado final: '$resultadoFinal'")

                val xpGanada = if (resultadoFinal == "GANASTE") GameConfig.XP_MINIJUEGO_GODOT else 0
                android.util.Log.d("GameViewModel", "[5] XP a sumar: $xpGanada")

                // Determinar nombre del juego según el package
                val nombreJuego = when (juegoActual) {
                    "com.example.atrapasalud" -> "AtrapaSalud"
                    "com.example.velocidad" -> "Velocidad"
                    else -> "Juego"
                }

                android.util.Log.d("GameViewModel", "[6] Nombre del juego: $nombreJuego")

                procesarResultadoJuegoUseCase(GameResult(
                    id       = 0,
                    userId   = uid,
                    name     = nombreJuego,
                    weight   = 10,
                    xpEarned = xpGanada,
                    date     = System.currentTimeMillis()
                ))

                android.util.Log.d("GameViewModel", "[7] Resultado guardado en BD")

                if (xpGanada > 0) {
                    agregarXpUseCase(uid, xpGanada)
                    android.util.Log.d("GameViewModel", "[8] XP sumado al usuario")
                }

                _uiState.update { it.copy(
                    juegoActivo      = false,
                    mensajeResultado = if (xpGanada > 0) "+$xpGanada XP ganados!" else "Sigue intentando"
                )}

                android.util.Log.d("GameViewModel", "✅ Proceso completado exitosamente")
            } catch (e: Exception) {
                android.util.Log.e("GameViewModel", "❌ Error:', e.message", e)
                _uiState.update { it.copy(
                    juegoActivo = false,
                    error       = e.message
                )}
            }
        }
    }
}

data class GameUiState(
    val juegoActivo: Boolean = false,
    val mensajeResultado: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)