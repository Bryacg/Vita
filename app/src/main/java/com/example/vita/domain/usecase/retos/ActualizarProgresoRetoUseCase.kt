package com.example.vita.domain.usecase.retos

import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

class ActualizarProgresoRetoUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(retoId: Int, cantidad: Int) {
        challengeRepository.updateProgress(retoId, cantidad)
    }
}
