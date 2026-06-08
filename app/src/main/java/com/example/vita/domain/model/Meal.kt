package com.example.vita.domain.model

/**
 * Registro de comidas con calorías y puntuación saludable.
 */
data class Meal(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val calories: Int,
    val healthyScore: Int,
    val date: Long
)
