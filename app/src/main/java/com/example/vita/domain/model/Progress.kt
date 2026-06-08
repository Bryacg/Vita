package com.example.vita.domain.model

/**
 * Modelo de dominio para representar el progreso del usuario.
 * Este modelo es puro (sin anotaciones de Room) y se usa en la capa de dominio.
 */
data class Progress(
    val id: Long = 0,
    val userId: String,
    val level: Int,
    val xp: Int,
    val weight: Float,
    val bmi: Float,
    val date: Long,
    val streakDays: Int
)
