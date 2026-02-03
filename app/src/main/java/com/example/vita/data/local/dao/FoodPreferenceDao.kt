package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.FoodPreferenceEntity

@Dao
interface FoodPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(pref: FoodPreferenceEntity)

    // CORRECCIÓN: Quitamos LIMIT 1 para traer TODOS los gustos (ej: Manzana, Pollo, etc.)
    @Query("SELECT * FROM food_preference WHERE userId = :userId")
    suspend fun getPreferencesByUserId(userId: String): List<FoodPreferenceEntity>

    @Update
    suspend fun updatePreference(pref: FoodPreferenceEntity)

    @Delete
    suspend fun deletePreference(pref: FoodPreferenceEntity)
}