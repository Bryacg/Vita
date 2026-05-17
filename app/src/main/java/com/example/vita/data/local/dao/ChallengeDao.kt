package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.ChallengeEntity

@Dao
interface ChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenger(challenger: ChallengeEntity)

    @Query("""
        SELECT * FROM challenge 
        WHERE userId = :uid 
        AND status IN ('ACTIVE', 'ACTIVO', 'PROGRESSO')
        ORDER BY deadline ASC
    """)
    suspend fun getActiveChallenges(uid: String): List<ChallengeEntity>

    /**
     * Retos DIARIOS creados hoy (entre 00:00:00 y 23:59:59 de hoy)
     */
    @Query("""
        SELECT * FROM challenge
        WHERE userId  = :uid
        AND   type    IN ('DIARIO', 'diario')
        AND   createdAt >= :startOfDay
        AND   createdAt <= :endOfDay
    """)
    suspend fun getDailyChallengesDeHoy(
        uid: String,
        startOfDay: Long,
        endOfDay: Long
    ): List<ChallengeEntity>

    /**
     * Retos SEMANALES creados esta semana (entre lunes 00:00:01 y domingo 23:59:59)
     */
    @Query("""
        SELECT * FROM challenge
        WHERE userId  = :uid
        AND   type    IN ('SEMANAL', 'semanal')
        AND   createdAt >= :startOfWeek
        AND   createdAt <= :endOfWeek
    """)
    suspend fun getSemanalesEstaSemana(
        uid: String,
        startOfWeek: Long,
        endOfWeek: Long
    ): List<ChallengeEntity>

    /**
     * Todos los retos para mostrar en pantalla:
     *   - Diarios creados HOY
     *   - Semanales creados esta semana (lunes–domingo)
     * Ordenados: activos → en progreso → completados → expirados
     */
    @Query("""
        SELECT * FROM challenge
        WHERE userId = :uid
        AND (
            (type IN ('DIARIO',  'diario')  AND createdAt >= :startOfDay  AND createdAt <= :endOfDay)
            OR
            (type IN ('SEMANAL', 'semanal') AND createdAt >= :startOfWeek AND createdAt <= :endOfWeek)
        )
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
    suspend fun getAllChallengesParaHoy(
        uid: String,
        startOfDay: Long,
        endOfDay: Long,
        startOfWeek: Long,
        endOfWeek: Long
    ): List<ChallengeEntity>

    /**
     * Marca como EXPIRED los retos cuyo deadline ya pasó y siguen activos
     */
    @Query("""
        UPDATE challenge
        SET status = 'EXPIRED'
        WHERE userId = :uid
        AND   deadline < :ahora
        AND   status IN ('ACTIVE', 'ACTIVO', 'PROGRESSO')
    """)
    suspend fun expirarRetosVencidos(uid: String, ahora: Long)

    @Query("UPDATE challenge SET status = 'COMPLETED' WHERE id = :challengeId")
    suspend fun completeChallenge(challengeId: Long)

    @Query("DELETE FROM challenge WHERE id = :challengeId")
    suspend fun deleteChallenge(challengeId: Long)
}