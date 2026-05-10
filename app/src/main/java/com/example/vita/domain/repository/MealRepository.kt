package com.example.vita.domain.repository

import com.example.vita.domain.model.Meal

interface MealRepository {
    suspend fun insertMeal(meal: Meal)
    suspend fun getMealsByUser(uid: String): List<Meal>
    suspend fun deleteMeal(id: Long)

    // Esta es la que resuelve tus errores en el HomeViewModel
    suspend fun getMealsByDate(userId: String, timestamp: Long): List<Meal>
}