package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.ProgresoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProgresoStreamUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    // Al devolver un Flow, la Home siempre tendrá los datos más recientes
    operator fun invoke(uid: String): Flow<Progress?> {
        return progresoRepository.getProgresoStream(uid)
    }
}