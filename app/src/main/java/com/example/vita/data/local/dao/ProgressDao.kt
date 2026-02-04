package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    // --- CONSULTAS DE LECTURA ---

    @Query("SELECT * FROM progress WHERE userId = :uid LIMIT 1")
    fun getProgresoStream(uid: String): Flow<ProgressEntity?>

    @Query("SELECT * FROM progress WHERE userId = :uid LIMIT 1")
    suspend fun getProgressByUser(uid: String): ProgressEntity?

    @Query("SELECT * FROM progress WHERE userId = :uid AND date = :date LIMIT 1")
    suspend fun getProgressByDate(uid: String, date: Long): ProgressEntity?

    @Query("SELECT SUM(xp) FROM progress WHERE userId = :uid")
    suspend fun getTotalXp(uid: String): Int?


    // --- OPERACIONES DE ESCRITURA ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Query("UPDATE progress SET xp = xp + :xp WHERE userId = :uid AND date = :date")
    suspend fun addXpHoy(uid: String, xp: Int, date: Long)

    @Query("UPDATE progress SET level = :level WHERE userId = :uid")
    suspend fun updateLevel(uid: String, level: Int)

    @Query("UPDATE progress SET bmi = :bmi WHERE userId = :uid")
    suspend fun updateBmi(uid: String, bmi: Double)

    @Query("UPDATE progress SET streakDays = :streak WHERE userId = :uid")
    suspend fun updateStreak(uid: String, streak: Int)

    @Query("UPDATE progress SET streakDays = 0 WHERE userId = :uid")
    suspend fun resetStreak(uid: String)

    // Mantenemos esta por compatibilidad si otros repositorios la usan,
    // pero la recomendada ahora es addXpHoy
    @Query("UPDATE progress SET xp = xp + :xp WHERE userId = :uid")
    suspend fun addXp(uid: String, xp: Int)
}