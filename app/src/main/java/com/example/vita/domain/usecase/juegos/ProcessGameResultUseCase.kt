package com.example.vita.domain.usecase.juegos

import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.GamesRepository
import javax.inject.Inject

class ProcesarResultadoJuegoUseCase @Inject constructor(
    private val GamesRepository: GamesRepository
) {
    suspend operator fun invoke(resultado: GameResult) {
        GamesRepository.insertResult(resultado)
    }
}
