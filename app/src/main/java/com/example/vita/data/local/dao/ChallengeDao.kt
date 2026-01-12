package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.ChallengeEntity

@Dao
interface ChallengeDao {
    // Obtiene los desafíos activos de un usuario.
    @Query("SELECT * FROM challenge WHERE userId = :uid AND status = 'ACTIVE'  ")
    suspend fun getActiveChallenges(uid: String): List<ChallengeEntity>

    // Actualiza el progreso actual de un desafío.
    @Query("UPDATE challenge SET currentValue = :currentValue WHERE id = :challengeId ")
    suspend fun updateProgress(challengeId: Long, currentValue: Int)

    // Marca un desafío como completado.
    @Query("UPDATE challenge SET status = 'COMPLETED' WHERE id = :challengeId")
    suspend fun completeChallenge(challengeId: Long)
    // Elimina un desafío.
    @Query("DELETE FROM challenge WHERE id = :challengeId")
    suspend fun deleteChallenge(challengeId: Long)
}
