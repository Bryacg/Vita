package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

class ObtenerRetosActivosUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(uid: String): List<Challenger> {
        return challengeRepository.getActiveChallenges(uid)
    }
}
