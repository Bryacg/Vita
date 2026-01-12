package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.repository.ProgresoRepository
import javax.inject.Inject


class TrackRachaUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    suspend operator fun invoke(uid: String, actividadHoy: Boolean): Int {
        val progreso = progresoRepository.getProgreso(uid)
        return if (progreso != null) {
            if (actividadHoy) {
                val nuevaRacha = progreso.streakDays + 1
                progresoRepository.actualizarRacha(uid, nuevaRacha)
                nuevaRacha
            } else {
                progresoRepository.resetearRacha(uid)
                0
            }
        } else 0
    }
}

