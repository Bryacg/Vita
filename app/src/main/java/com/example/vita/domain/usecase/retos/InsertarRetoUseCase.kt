package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

class InsertarRetoUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(reto: Challenger) {
        challengeRepository.insertChallenge(reto)
    }
}
