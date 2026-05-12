package com.example.vita.data.repository

import com.example.vita.data.local.dao.FoodDao
import com.example.vita.data.local.dao.FoodPreferenceDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Food
import com.example.vita.domain.model.FoodPreference
import com.example.vita.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao,
    private val preferenceDao: FoodPreferenceDao
) : FoodRepository {

    // ✅ JOIN atómico: ya no hay N+1 ni nulls silenciosos
    override suspend fun getUserFoodPreferences(userId: String): List<Pair<Food, FoodPreference>> =
        withContext(Dispatchers.IO) {
            preferenceDao.getPreferencesConAlimento(userId).map { tupla ->
                val food = Food(
                    id       = tupla.foodId,
                    name     = tupla.foodName,
                    category = tupla.foodCategory
                )
                val pref = FoodPreference(
                    id             = tupla.id,
                    userId         = tupla.userId,
                    foodId         = tupla.foodId,
                    preferenceType = tupla.preferenceType
                )
                Pair(food, pref)
            }
        }

    override suspend fun getFoodByName(name: String): Food? =
        withContext(Dispatchers.IO) {
            foodDao.getFoodByName(name)?.toDomain()
        }

    override suspend fun saveFood(food: Food): Long =
        withContext(Dispatchers.IO) {
            foodDao.insertFood(food.toEntity())
        }

    override suspend fun savePreference(preference: FoodPreference) =
        withContext(Dispatchers.IO) {
            preferenceDao.insertPreference(preference.toEntity())
        }

    override suspend fun deletePreference(preference: FoodPreference) =
        withContext(Dispatchers.IO) {
            preferenceDao.deletePreference(preference.toEntity())
        }
}