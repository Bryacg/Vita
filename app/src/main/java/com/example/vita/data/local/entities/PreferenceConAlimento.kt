package com.example.vita.data.local.entities

import androidx.room.ColumnInfo

/**
 * Resultado de la query JOIN entre food_preference y food.
 * Evita el problema de N+1 consultas y los nulls silenciosos.
 */
data class PreferenceConAlimento(
    // Campos de food_preference
    val id: Long,
    val userId: String,
    val foodId: Long,
    val preferenceType: String,
    // Campos de food (con alias para evitar conflicto de nombres)
    @ColumnInfo(name = "food_name")     val foodName: String,
    @ColumnInfo(name = "food_category") val foodCategory: String
)