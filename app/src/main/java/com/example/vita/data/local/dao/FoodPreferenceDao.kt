package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.FoodPreferenceEntity
import com.example.vita.data.local.entities.PreferenceConAlimento

@Dao
interface FoodPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(pref: FoodPreferenceEntity)

    @Query("SELECT * FROM food_preference WHERE userId = :userId")
    suspend fun getPreferencesByUserId(userId: String): List<FoodPreferenceEntity>

    // ✅ JOIN atómico: una sola query en lugar de N+1 llamadas a getFoodById
    @Query("""
        SELECT fp.id,
               fp.userId,
               fp.foodId,
               fp.preferenceType,
               f.name     AS food_name,
               f.category AS food_category
        FROM food_preference fp
        INNER JOIN food f ON fp.foodId = f.id
        WHERE fp.userId = :userId
    """)
    suspend fun getPreferencesConAlimento(userId: String): List<PreferenceConAlimento>

    @Update
    suspend fun updatePreference(pref: FoodPreferenceEntity)

    @Delete
    suspend fun deletePreference(pref: FoodPreferenceEntity)
}