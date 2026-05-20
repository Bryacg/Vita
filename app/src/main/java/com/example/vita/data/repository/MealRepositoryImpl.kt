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

@Singleton
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
            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0);      cal.set(Calendar.MILLISECOND, 0)
            val start = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val end = cal.timeInMillis

            dao.getMealsByDateRange(userId, start, end).map { it.toDomain() }
        }

    /**
     * Agrupa las comidas de los últimos 7 días por fecha y suma
     * las calorías de cada día.
     * Devuelve exactamente 7 valores (índice 0 = hace 6 días, índice 6 = hoy).
     */
    override suspend fun getCaloriasUltimaSemana(uid: String): List<Int> =
        withContext(Dispatchers.IO) {

            // Rango completo: inicio de hace 6 días → fin de hoy
            val startDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -6)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val comidas = dao.getMealsByDateRange(uid, startDate, endDate)

            // Función auxiliar: normaliza un timestamp al inicio de su día
            fun startOfDay(ts: Long): Long = Calendar.getInstance().apply {
                timeInMillis = ts
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Agrupa calorías por día
            val caloriasPorDia = mutableMapOf<Long, Int>()
            comidas.forEach { meal ->
                val dia = startOfDay(meal.date)
                caloriasPorDia[dia] = (caloriasPorDia[dia] ?: 0) + meal.calories
            }

            // Construye la lista de 7 días (hace 6 → hoy)
            (6 downTo 0).map { daysAgo ->
                val diaKey = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -daysAgo)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                caloriasPorDia[diaKey] ?: 0
            }
        }
}