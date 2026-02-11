package com.example.vita.domain.model

enum class AchievementType(val id: Long, val title: String, val desc: String) {
    PRIMER_PASO(1, "El Primer Paso", "Completaste tu primer reto diario."),
    SUBIDA_NIVEL(2, "Ascenso", "Lograste subir al nivel 2."),
    CONSTANCIA_SEMANAL(3, "Guerrero Semanal", "Completaste todos los retos de la semana."),
    RACHA_FUEGO(4, "En racha", "Mantuviste una racha de 7 días.")
}