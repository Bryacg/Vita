package com.example.vita.domain.model

object AchievementEvaluator {

    fun checkAchievements(
        totalRetosCompletados: Int,
        nivelActual: Int,
        retosSemanalesCompletados: Int,
        rachaActual: Int
    ): List<AchievementType> {
        val unlocked = mutableListOf<AchievementType>()

        // Lógica para "Primer Reto"
        if (totalRetosCompletados >= 1) {
            unlocked.add(AchievementType.PRIMER_PASO)
        }

        // Lógica para "Subida de Nivel"
        if (nivelActual >= 2) {
            unlocked.add(AchievementType.SUBIDA_NIVEL)
        }

        // Lógica para "Retos Semanales"
        if (retosSemanalesCompletados >= GameConfig.MAX_RETOS_SEMANALES) {
            unlocked.add(AchievementType.CONSTANCIA_SEMANAL)
        }

        // Lógica para "Racha de 7 días"
        if (rachaActual >= 7) {
            unlocked.add(AchievementType.RACHA_FUEGO)
        }

        return unlocked
    }
}