package com.example.vita.domain.model

/**
 * Resultados de minijuegos Godot.
 */
data class GameResult(
    val id: Long=0 ,
    val userId: String,
    val name: String,      // Nombre del evento
    val weight: Int,      // Peso o importancia del evento.
    val xpEarned: Int,    // Experiencia ganada por el evento.
    val date: Long
)
