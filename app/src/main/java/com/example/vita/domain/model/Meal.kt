package com.example.vita.domain.model

/**
 * Registro de comidas con calorías y puntuación saludable.
 */
data class Meal(
    val id: Long ,
    val userId: String,
    val name: String,    // Nombre de la comida ingerida.
    val calories: Int,  // Cantidad de calorías estimadas.
    val healthyScore: Int, // 0–100  // Puntaje de calidad nutricional (0–100).
    val date: Long         // timestamp // Fecha y hora del consumo en formato timestamp
)
