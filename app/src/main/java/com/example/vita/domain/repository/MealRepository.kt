package com.example.vita.domain.repository

import com.example.vita.domain.model.Meal

interface MealRepository {
    suspend fun insertMeal(meal: Meal)
    suspend fun getMealsByUser(uid: String): List<Meal>
    suspend fun deleteMeal(id: Long)
    suspend fun getMealsByDate(userId: String, timestamp: Long): List<Meal>

    /**
     * Devuelve una lista de 7 enteros — uno por día, ordenado
     * de hace 6 días hasta hoy — con las calorías totales consumidas.
     * Si un día no tiene registros devuelve 0.
     */
    suspend fun getCaloriasUltimaSemana(uid: String): List<Int>
}