package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.MealEntity

@Dao
interface MealDao {
    // Inserta una comida registrada por el usuario.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    // Obtiene todas las comidas de un usuario ordenadas por fecha.
    @Query("SELECT * FROM meal  WHERE userId = :uid ORDER BY date DESC")
    suspend fun getMealsByUser(uid: String): List<MealEntity>

    //borra una comida por su ID.
    @Query("DELETE FROM meal WHERE id = :id")
    suspend fun deleteMeal(id: Int)
}
