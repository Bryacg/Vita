package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.FoodPreferenceEntity

@Dao
interface FoodPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(pref: FoodPreferenceEntity)

    @Query("SELECT * FROM food_preference WHERE userId = :uid LIMIT 1")
    suspend fun getPreferenceByUser(uid: String): FoodPreferenceEntity?

    @Update
    suspend fun updatePreference(pref: FoodPreferenceEntity)

    @Delete
    suspend fun deletePreference(pref: FoodPreferenceEntity)
}
