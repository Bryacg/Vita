package com.example.vita.domain.usecase.achievement

import com.example.vita.domain.model.Achievement
import javax.inject.Inject

/**
 * Evalúa qué logros están desbloqueados dado el estado actual del usuario.
 * Es una función pura: misma entrada → misma salida, sin efectos secundarios.
 */
class EvaluarLogrosUseCase @Inject constructor() {

    operator fun invoke(
        uid: String,
        nivelActual: Int,
        cantidadPreferencias: Int,
        tienePerfilCompleto: Boolean,
        aguaActiva: Boolean
    ): List<Achievement> {
        val input = AchievementInput(
            uid = uid,
            nivelActual = nivelActual,
            cantidadPreferencias = cantidadPreferencias,
            tienePerfilCompleto = tienePerfilCompleto,
            aguaActiva = aguaActiva
        )
        return AchievementRules.evaluar(input)
    }
}