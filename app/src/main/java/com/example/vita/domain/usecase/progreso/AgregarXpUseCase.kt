package com.example.vita.domain.usecase.progreso

import com.example.vita.core.DateTimeUtils
import com.example.vita.domain.model.LevelCalculator
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class AgregarXpUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, xpGanado: Int) {
        val hoy = DateTimeUtils.getTodayMillis()      // ✅ usando DateTimeUtils del core
        val ayer = DateTimeUtils.getDaysAgoMillis(1)

        val progresoDeHoy = progresoRepository.getProgresoPorFecha(uid, hoy)

        if (progresoDeHoy == null) {
            // Es un día nuevo: verificamos la racha mirando ayer
            val progresoAyer = progresoRepository.getProgresoPorFecha(uid, ayer)
            val nuevaRacha = if (progresoAyer != null) progresoAyer.streakDays + 1 else 1

            // ✅ Calculamos el nivel correcto ANTES de crear el registro
            val xpActual = progresoRepository.getTotalXpDeSiempre(uid)
            val xpFuturo = xpActual + xpGanado
            val infoNivelActual = LevelCalculator.calculateLevel(xpFuturo)

            val nuevoProgreso = Progress(
                id = 0,
                userId = uid,
                xp = xpGanado,
                level = infoNivelActual.level, // ✅ nivel calculado, no hardcodeado
                date = hoy,
                streakDays = nuevaRacha,
                bmi = 0.0f,
                weight = 0.0f
            )
            progresoRepository.insertarProgreso(nuevoProgreso)
        } else {
            // Ya existe el día: sumamos XP al registro existente
            progresoRepository.agregarXpHoy(uid, xpGanado, hoy)
        }

        // Sincronizamos el total global en la tabla de usuarios
        val totalXpAcumulado = progresoRepository.getTotalXpDeSiempre(uid)
        val infoNivel = LevelCalculator.calculateLevel(totalXpAcumulado)
        userRepository.updateUserXpAndLevel(uid, totalXpAcumulado, infoNivel.level)
    }
}