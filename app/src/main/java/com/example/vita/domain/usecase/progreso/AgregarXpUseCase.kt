package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.model.LevelCalculator
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class AgregarXpUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, xpGanado: Int) {
        // --- COPIA DESDE AQUÍ ---
        println("DEBUG: Iniciando suma de $xpGanado XP para $uid")

        // 1. Suma el XP en la tabla 'progress'
        progresoRepository.agregarXp(uid, xpGanado)

        // 2. Lee el nuevo total para confirmar la suma
        val progresoActual = progresoRepository.getProgreso(uid)
        println("DEBUG: XP en tabla progress tras sumar: ${progresoActual?.xp}")

        progresoActual?.let {
            // 3. Calcula el nivel con el total (ej: 160)
            val infoNivel = LevelCalculator.calculateLevel(it.xp)
            println("DEBUG: Nuevo nivel calculado: ${infoNivel.level}")

            // 4. Sincroniza con la tabla 'users' (lo que lee la CardInf)
            userRepository.updateUserXpAndLevel(uid, it.xp, infoNivel.level)
            println("DEBUG: Llamada a sincronizar usuario enviada")
        }
        // --- HASTA AQUÍ ---
    }
}