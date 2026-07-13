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

    private val _navegarAJuego = MutableSharedFlow<String>()
    val navegarAJuego: SharedFlow<String> = _navegarAJuego.asSharedFlow()

    /**
     * Se llama al presionar "Jugar ahora".
     * Otorga la XP de inmediato (sin esperar resultado de Godot vía
     * broadcast/intent, que resultó poco confiable en ciertos
     * dispositivos/fabricantes) y luego lanza el juego para que el
     * usuario disfrute la experiencia visual del minijuego.
     */
    fun solicitarAbrirJuego(packageName: String = "com.example.atrapasalud") {
        val uid = authRepository.getCurrentUserId() ?: run {
            android.util.Log.e("GameViewModel", "❌ No hay usuario autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(juegoActivo = true, mensajeResultado = null) }

            val nombreJuego = when (packageName) {
                "com.example.atrapasalud" -> "AtrapaSalud"
                "com.example.velocidad" -> "Velocidad"
                else -> "Juego"
            }

            try {
                // 1. Otorgamos la XP inmediatamente, sin depender de Godot
                procesarResultadoJuegoUseCase(
                    GameResult(
                        id = 0,
                        userId = uid,
                        name = nombreJuego,
                        weight = 10,
                        xpEarned = GameConfig.XP_MINIJUEGO_GODOT,
                        date = System.currentTimeMillis()
                    )
                )
                agregarXpUseCase(uid, GameConfig.XP_MINIJUEGO_GODOT)

                _uiState.update {
                    it.copy(mensajeResultado = "+${GameConfig.XP_MINIJUEGO_GODOT} XP ganados!")
                }

                android.util.Log.d("GameViewModel", "✅ XP otorgada de inmediato al iniciar juego")
            } catch (e: Exception) {
                android.util.Log.e("GameViewModel", "❌ Error otorgando XP: ${e.message}", e)
                _uiState.update { it.copy(error = e.message) }
            }

            // 2. Lanzamos el juego para que el usuario lo disfrute
            _navegarAJuego.emit(packageName)
        }
    }

    /**
     * Se llama cuando el usuario regresa de Godot (independientemente
     * de si llegó resultado por broadcast/archivo o no). Ya no procesa
     * XP aquí — solo limpia el estado de "juego activo".
     */
    fun onRegresarDeJuego(resultado: String?) {
        android.util.Log.d(
            "GameViewModel",
            "Regresó de Godot. Resultado recibido (ignorado para XP): '$resultado'"
        )
        _uiState.update { it.copy(juegoActivo = false) }
    }
}

data class GameUiState(
    val juegoActivo: Boolean = false,
    val mensajeResultado: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)