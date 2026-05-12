package com.example.vita.domain.usecase.achievement

import com.example.vita.domain.model.Achievement

/**
 * Datos necesarios para evaluar los logros.
 * El ViewModel carga estos datos y los pasa aquí.
 */
data class AchievementInput(
    val uid: String,
    val nivelActual: Int,
    val cantidadPreferencias: Int,
    val tienePerfilCompleto: Boolean,
    val aguaActiva: Boolean
)

/**
 * Fuente de verdad de las reglas de logros.
 * Vive en Domain, no en el ViewModel.
 */
object AchievementRules {

    private val LOGROS_MAESTROS = listOf(
        Triple("Nivel Principiante", "Alcanza el nivel 2", 2),
        Triple("Maestro Gourmet",   "Agrega 3 o más preferencias alimentarias", 3),
        Triple("Perfil Completo",   "Registra tus datos biométricos", 1),
        Triple("Racha de Agua",     "Activa los recordatorios de hidratación", 1)
    )

    fun evaluar(input: AchievementInput): List<Achievement> {
        return LOGROS_MAESTROS.mapIndexed { index, (nombre, desc, meta) ->
            val desbloqueado = when (nombre) {
                "Nivel Principiante" -> input.nivelActual >= meta
                "Maestro Gourmet"   -> input.cantidadPreferencias >= meta
                "Perfil Completo"   -> input.tienePerfilCompleto
                "Racha de Agua"     -> input.aguaActiva
                else -> false
            }
            Achievement(
                id = index.toLong(),
                userId = input.uid,
                name = nombre,
                description = desc,
                unlocked = desbloqueado
            )
        }
    }
}