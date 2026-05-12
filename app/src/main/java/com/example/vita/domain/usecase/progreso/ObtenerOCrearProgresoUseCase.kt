package com.example.vita.domain.usecase.progreso

import com.example.vita.core.DateTimeUtils
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.ProgresoRepository
import javax.inject.Inject

/**
 * Devuelve el progreso del usuario.
 * Si no existe (primer inicio), lo crea con valores en 0.
 * Esta lógica de negocio estaba en HomeViewModel — aquí es donde corresponde.
 */
class ObtenerOCrearProgresoUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository
) {
    suspend operator fun invoke(uid: String): Progress {
        return progresoRepository.getProgreso(uid) ?: crearProgresoInicial(uid)
    }

    private suspend fun crearProgresoInicial(uid: String): Progress {
        val nuevo = Progress(
            id = 0,
            userId = uid,
            level = 1,
            xp = 0,
            streakDays = 0,           // ✅ empieza en 0
            bmi = 0f,
            weight = 0f,
            date = DateTimeUtils.getTodayMillis() // ✅ fecha normalizada
        )
        progresoRepository.insertarProgreso(nuevo)
        return nuevo
    }
}