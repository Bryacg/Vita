package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

/**
 * 1. Expira los retos cuyo deadline ya pasó.
 * 2. Devuelve diarios de hoy + semanales de esta semana.
 */
class ObtenerRetosDeHoyUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(uid: String): List<Challenger> {
        challengeRepository.expirarRetosVencidos(uid)
        return challengeRepository.getAllChallengesParaHoy(uid)
    }
}