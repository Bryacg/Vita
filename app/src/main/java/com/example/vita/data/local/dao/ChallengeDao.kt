package com.example.vita.data.local.dao
import androidx.room.*
import com.example.vita.data.local.entities.ChallengeEntity
import com.example.vita.domain.model.Challenger

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    // Incluimos PROGRESSO para que no desaparezcan al actualizarse
    @Query("""
        SELECT * FROM challenge 
        WHERE userId = :uid 
        AND status IN ('ACTIVE', 'ACTIVO', 'PROGRESSO')
        ORDER BY deadline ASC
    """)
    suspend fun getActiveChallenges(uid: String): List<ChallengeEntity>

    @Update
    suspend fun updateChallenger(challenger: ChallengeEntity)

    @Query("UPDATE challenge SET status = 'COMPLETED' WHERE id = :challengeId")
    suspend fun completeChallenge(challengeId: Long)

    @Query("DELETE FROM challenge WHERE id = :challengeId")
    suspend fun deleteChallenge(challengeId: Long)
}