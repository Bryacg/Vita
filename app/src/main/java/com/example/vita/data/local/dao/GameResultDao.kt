package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.GameResultEntity

@Dao
interface GameResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: GameResultEntity)

    @Query("SELECT * FROM game_result WHERE userId = :uid ORDER BY date DESC")
    suspend fun getResultsByUser(uid: String): List<GameResultEntity>

    @Query("SELECT SUM(xpEarned) FROM game_result WHERE userId = :uid")
    suspend fun getTotalXpFromGames(uid: String): Int?
}
