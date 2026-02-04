package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.model.LevelCalculator
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import java.util.Calendar
import javax.inject.Inject

class AgregarXpUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, xpGanado: Int) {
        val hoy = DateUtils.getTodayMillis()
        val ayer = hoy - (24 * 60 * 60 * 1000) // Restamos un día en milisegundos

        // 1. Buscamos si el usuario ya hizo algo hoy
        val progresoDeHoy = progresoRepository.getProgresoPorFecha(uid, hoy)

        if (progresoDeHoy == null) {
            // ES UN DÍA NUEVO: Verificamos la racha mirando ayer
            val progresoAyer = progresoRepository.getProgresoPorFecha(uid, ayer)

            // Si ayer hubo progreso, racha + 1, si no, empezamos en 1
            val nuevaRacha = if (progresoAyer != null) {
                progresoAyer.streakDays + 1
            } else {
                1
            }

            val nuevoProgreso = Progress(
                id = 0,
                userId = uid,
                xp = xpGanado,
                level = 1,
                date = hoy,
                streakDays = nuevaRacha,
                bmi = 0.0f,    // Valor inicial
                weight = 0.0f  // Valor inicial
            )
            progresoRepository.insertarProgreso(nuevoProgreso)
        } else {
            // YA EXISTE EL DÍA: Actualizamos el registro de hoy
            progresoRepository.agregarXpHoy(uid, xpGanado, hoy)
        }

        // 2. Sincronizamos con el total GLOBAL para la Card de la Home
        val totalXpAcumulado = progresoRepository.getTotalXpDeSiempre(uid)
        val infoNivel = LevelCalculator.calculateLevel(totalXpAcumulado)

        // Actualizamos la tabla de Usuario (para que la CardInf se vea en tiempo real)
        userRepository.updateUserXpAndLevel(uid, totalXpAcumulado, infoNivel.level)
    }
}

object DateUtils {
    fun getTodayMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}