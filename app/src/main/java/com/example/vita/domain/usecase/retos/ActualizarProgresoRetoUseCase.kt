package com.example.vita.domain.usecase.retos

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

// 1. Asegúrate de que el nombre de la CLASE empiece con Mayúscula
class ActualizarProgresoRetoUseCase @Inject constructor(
    // 2. Verifica que ChallengeRepository esté bien importado y escrito
    private val repository: ChallengeRepository
) {
    suspend operator fun invoke(challenger: com.example.vita.domain.model.Challenger) {
        repository.updateReto(challenger)
    }
}