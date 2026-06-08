package com.example.vita.domain.model

/**
 * Resultados de minijuegos Godot.
 */
data class GameResult(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val weight: Int,
    val xpEarned: Int,
    val date: Long
)
