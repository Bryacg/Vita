package com.example.vita.data.repository

import com.example.vita.data.local.dao.FoodDao
import com.example.vita.data.local.dao.FoodPreferenceDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.repository.FoodRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao,
    private val preferenceDao: FoodPreferenceDao
) : FoodRepository {

    override suspend fun getUserFoodPreferences(userId: String): List<Pair<Food, FoodPreference>> {
        val prefEntities = preferenceDao.getPreferencesByUserId(userId)
        return prefEntities.mapNotNull { prefEntity ->
            val foodEntity = foodDao.getFoodById(prefEntity.foodId)
            if (foodEntity != null) {
                Pair(foodEntity.toDomain(), prefEntity.toDomain())
            } else null
        }
    }

    override suspend fun getFoodByName(name: String): Food? {
        return foodDao.getFoodByName(name)?.toDomain()
    }

    override suspend fun saveFood(food: Food): Long {
        return foodDao.insertFood(food.toEntity())
    }

    override suspend fun savePreference(preference: FoodPreference) {
        preferenceDao.insertPreference(preference.toEntity())
    }

    override suspend fun deletePreference(preference: FoodPreference) {
        preferenceDao.deletePreference(preference.toEntity())
    }
}