package com.example.vita.domain.model

data class FoodPreference(
    val id: Long ,
    val userId: String,   // Usuario dueño de la preferencia.
    val foodId: Long,     // Alimento al que se asocia la preferencia.
    val preferenceType: String
)
