package com.example.vita.data.repository
import com.example.vita.data.local.dao.MealDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Meal
import com.example.vita.domain.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val dao: MealDao
) : MealRepository {

    override suspend fun insertMeal(meal: Meal) = withContext(Dispatchers.IO) {
        dao.insertMeal(meal.toEntity())
    }

    override suspend fun getMealsByUser(uid: String): List<Meal> = withContext(Dispatchers.IO) {
        dao.getMealsByUser(uid).map { it.toDomain() }
    }
    override suspend fun deleteMeal(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteMeal(id)
    }


}
