package com.example.vita.domain.model

object LevelCalculator {
    /**
     * Determina el nivel y rango basado estrictamente en el XP acumulado.
     */
    fun calculateLevel(xp: Int): LevelResult {
        return when (xp) {
            in 0..500 -> LevelResult(1, "Aprendiz", 0..500)
            in 501..1500 -> LevelResult(2, "Explorador", 501..1500)
            in 1501..3000 -> LevelResult(3, "Entusiasta", 1501..3000)
            in 3001..5000 -> LevelResult(4, "Especialista", 3001..5000)
            in 5001..7500 -> LevelResult(5, "Nutri-Guerrero", 5001..7500)
            else -> LevelResult(6, "Maestro", 7501..100_000)
        }
    }
}

data class LevelResult(val level: Int, val title: String, val range: IntRange)