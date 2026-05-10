package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.MealEntity

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Query("SELECT * FROM meal WHERE userId = :uid ORDER BY date DESC")
    suspend fun getMealsByUser(uid: String): List<MealEntity>

    // NUEVA: Necesaria para el HomeViewModel
    @Query("SELECT * FROM meal WHERE userId = :uid AND date >= :start AND date <= :end")
    suspend fun getMealsByDateRange(uid: String, start: Long, end: Long): List<MealEntity>

    @Query("DELETE FROM meal WHERE id = :id")
    suspend fun deleteMeal(id: Long) // Cambiado a Long para ser consistente con la PK
}
