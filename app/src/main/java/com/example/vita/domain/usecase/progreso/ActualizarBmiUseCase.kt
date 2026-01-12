package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.repository.ProgresoRepository
import javax.inject.Inject

class ActualizarBmiUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    suspend operator fun invoke(uid: String, nuevoBmi: Double) {
        progresoRepository.actualizarBmi(uid, nuevoBmi)
    }
}