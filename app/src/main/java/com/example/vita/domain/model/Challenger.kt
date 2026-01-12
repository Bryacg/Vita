package com.example.vita.domain.model

/**
 * Retos diarios o semanales.
 */
data class Challenger(
    val id: Long = 0,
    val userId: String,
    val name: String,     // Nombre del desafío
    val description: String, // Descripción del objetivo
    val type: String,  // Tipo de desafío (semanal, diario).
    val targetValue: Int,    // Valor objetivo que debe alcanzarse
    val currentValue: Int = 0,  // Progreso actual del desafío.
    val status: String, // Estado del desafío: ACTIVE, COMPLETED, EXPIRED
    val deadline: Long
)
