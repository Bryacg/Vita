package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

/**
 * Caso de uso principal para la pantalla de Retos.
 *
 * Flujo:
 *  1. Expira los retos cuyo deadline ya pasó.
 *  2. Devuelve TODOS los retos creados HOY (activos, en progreso,
 *     completados y expirados) para que la pantalla los muestre con
 *     su estado real.
 */
class ObtenerRetosDeHoyUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(uid: String): List<Challenger> {
        // 1. Marcar expirados antes de leer
        challengeRepository.expirarRetosVencidos(uid)
        // 2. Devolver lo que hay hoy
        return challengeRepository.getAllChallengesDeHoy(uid)
    }
}