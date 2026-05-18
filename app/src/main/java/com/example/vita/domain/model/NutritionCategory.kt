package com.example.vita.domain.model

enum class NutritionCategory {
    MUY_SALUDABLE,
    SALUDABLE,
    REGULAR,
    POCO_SANO
}

data class NutritionResult(
    val healthyScore: Int,
    val category: String,
    val nutritionCategory: NutritionCategory,
    val xpEarned: Int,
    val isOverCalorieLimit: Boolean
)

