package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.repository.ProgresoRepository
import javax.inject.Inject

class ResetearRachaUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    suspend operator fun invoke(uid: String) {
        progresoRepository.resetearRacha(uid)
    }
}
