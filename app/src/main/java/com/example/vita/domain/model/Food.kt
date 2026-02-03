package com.example.vita.domain.model

/**
 * ingreso de comida.
 */
data class Food(
    val id: Long = 0,             // Agregamos el = 0
    val name: String,
    val category: String = "General" // Agregamos el valor por defecto
)
