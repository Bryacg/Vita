package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.ProgressEntity
import kotlinx.coroutines.flow.Flow // Importación necesaria

@Dao
interface ProgressDao {
    // 1. FUNCIÓN FALTANTE: Permite la reactividad en la CardInf
    @Query("SELECT * FROM progress WHERE userId = :uid LIMIT 1")
    fun getProgresoStream(uid: String): Flow<ProgressEntity?> // SIN 'suspend'

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :uid LIMIT 1")
    suspend fun getProgressByUser(uid: String): ProgressEntity?

    @Query("UPDATE progress SET xp = xp + :xp WHERE userId = :uid")
    suspend fun addXp(uid: String, xp: Int)

    @Query("UPDATE progress SET level = :level WHERE userId = :uid")
    suspend fun updateLevel(uid: String, level: Int)

    @Query("UPDATE progress SET bmi = :bmi WHERE userId = :uid")
    suspend fun updateBmi(uid: String, bmi: Double)

    @Query("UPDATE progress SET streakDays = :streak WHERE userId = :uid")
    suspend fun updateStreak(uid: String, streak: Int)

    @Query("UPDATE progress SET streakDays = 0 WHERE userId = :uid")
    suspend fun resetStreak(uid: String)
}