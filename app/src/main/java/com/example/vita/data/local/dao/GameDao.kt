package com.example.vita.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vita.data.local.entities.GameEntity

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(gameResult: GameEntity)

    @Query("SELECT * FROM game_result WHERE userId = :userId ORDER BY date DESC")
    suspend fun getResultsByUser(userId: String): List<GameEntity>

    /**
     * Esta función es vital para la gamificación.
     * Actualiza el nivel (XP) del usuario directamente en la tabla de usuarios.
     * Nota: Asegúrate de que tu UserEntity tenga el campo 'nivel'.
     */
    @Query("UPDATE users SET currentXp = currentXp + :xp WHERE idUsuario = :userId")
    suspend fun updatePlayerXp(userId: String, xp: Int)
}