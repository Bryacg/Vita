package com.example.vita.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vita.data.local.entities.GameResultEntity

@Dao
interface GameResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameResult(entity: GameResultEntity): Long

    @Query("SELECT * FROM game_result WHERE userId = :uid")
    suspend fun getResultsByUserId(uid: String): List<GameResultEntity>

    @Query("SELECT SUM(xpEarned) FROM game_result WHERE userId = :uid")
    suspend fun getSumXpByUserId(uid: String): Int?
}
