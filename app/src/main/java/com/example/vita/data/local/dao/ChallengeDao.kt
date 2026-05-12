package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.ChallengeEntity

@Dao
interface ChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenger(challenger: ChallengeEntity)

    // Retos activos (para lógica interna)
    @Query("""
        SELECT * FROM challenge 
        WHERE userId = :uid 
        AND status IN ('ACTIVE', 'ACTIVO', 'PROGRESSO')
        ORDER BY deadline ASC
    """)
    suspend fun getActiveChallenges(uid: String): List<ChallengeEntity>

    // ✅ TODOS los retos de HOY: activos, completados Y expirados
    @Query("""
        SELECT * FROM challenge
        WHERE userId  = :uid
        AND createdAt >= :startOfDay
        AND createdAt <  :endOfDay
        ORDER BY
            CASE status
                WHEN 'ACTIVO'    THEN 1
                WHEN 'ACTIVE'    THEN 1
                WHEN 'PROGRESSO' THEN 2
                WHEN 'COMPLETED' THEN 3
                ELSE 4
            END ASC,
            deadline ASC
    """)
    suspend fun getAllChallengesDeHoy(
        uid: String,
        startOfDay: Long,
        endOfDay: Long
    ): List<ChallengeEntity>

    // ✅ Marca como EXPIRED los que pasaron su deadline y siguen activos
    @Query("""
        UPDATE challenge
        SET status = 'EXPIRED'
        WHERE userId = :uid
        AND deadline < :ahora
        AND status IN ('ACTIVE', 'ACTIVO', 'PROGRESSO')
    """)
    suspend fun expirarRetosVencidos(uid: String, ahora: Long)

    @Query("UPDATE challenge SET status = 'COMPLETED' WHERE id = :challengeId")
    suspend fun completeChallenge(challengeId: Long)

    @Query("DELETE FROM challenge WHERE id = :challengeId")
    suspend fun deleteChallenge(challengeId: Long)
}