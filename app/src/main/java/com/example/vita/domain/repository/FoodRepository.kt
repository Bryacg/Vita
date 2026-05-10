package com.example.vita.domain.repository

import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference

interface FoodRepository {
    suspend fun getUserFoodPreferences(userId: String): List<Pair<Food, FoodPreference>>
    suspend fun savePreference(preference: FoodPreference)
    suspend fun deletePreference(preference: FoodPreference) //
    suspend fun getFoodByName(name: String): Food?           //
    suspend fun saveFood(food: Food): Long
}