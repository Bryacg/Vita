package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.repository.ProgresoRepository
import javax.inject.Inject

class ActualizarNivelUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    suspend operator fun invoke(uid: String, nuevoNivel: Int): Int {
        progresoRepository.actualizarNivel(uid, nuevoNivel)
        return nuevoNivel
    }
}
