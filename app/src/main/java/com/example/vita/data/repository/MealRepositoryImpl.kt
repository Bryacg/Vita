package com.example.vita.data.repository

import com.example.vita.data.local.dao.MealDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Meal
import com.example.vita.domain.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // ✅ agregado
class MealRepositoryImpl @Inject constructor(
    private val dao: MealDao
) : MealRepository {

    override suspend fun insertMeal(meal: Meal) = withContext(Dispatchers.IO) {
        dao.insertMeal(meal.toEntity())
    }

    override suspend fun getMealsByUser(uid: String): List<Meal> = withContext(Dispatchers.IO) {
        dao.getMealsByUser(uid).map { it.toDomain() }
    }

    override suspend fun deleteMeal(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteMeal(id)
    }

    override suspend fun getMealsByDate(userId: String, timestamp: Long): List<Meal> =
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val start = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val end = calendar.timeInMillis

            dao.getMealsByDateRange(userId, start, end).map { it.toDomain() }
        }
}
