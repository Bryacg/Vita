package com.example.vita.domain.usecase.godot

import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.GameRepository
import javax.inject.Inject

class ProcesarResultadoJuegoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(resultadoGodot: String) {
        val uid = authRepository.getCurrentUserId() ?: return

        // 1. Determinar cuánta XP ganó
        val xpGanada = if (resultadoGodot == "GANASTE") 50 else 0

        // 2. Construir el objeto de resultado
        val nuevoResultado = GameResult(
            id = 0,
            userId = uid,
            name = "AtrapaSalud",
            weight = 10,
            xpEarned = xpGanada,
            date = System.currentTimeMillis()
        )

        // 3. Guardar el historial en la base de datos
        gameRepository.saveGameResult(nuevoResultado)

        // 4. Si ganó, actualizar la XP global del usuario usando el GameRepository
        if (xpGanada > 0) {
            gameRepository.addXpToUser(uid, xpGanada)
        }
    }
}