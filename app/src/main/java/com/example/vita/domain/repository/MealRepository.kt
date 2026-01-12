package com.example.vita.domain.repository

import com.example.vita.domain.model.Meal

interface MealRepository {
    suspend fun insertMeal(meal: Meal)
    suspend fun getMealsByUser(uid: String): List<Meal>

    suspend fun deleteMeal(id: Int)
}
