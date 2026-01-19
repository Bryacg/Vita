package com.example.vita.domain.usecase.juegos

import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.GamesRepository
import javax.inject.Inject

class ProcesarResultadoJuegoUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    // Debe recibir el OBJETO completo
    suspend operator fun invoke(gameResult: GameResult): GameResult {
        return repository.saveGameResult(gameResult)
    }
}