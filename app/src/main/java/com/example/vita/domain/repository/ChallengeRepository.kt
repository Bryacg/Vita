package com.example.vita.domain.repository

import com.example.vita.domain.model.Challenger

interface ChallengeRepository {
    suspend fun generarYGuardarRetos(uid: String, nombre: String): List<Challenger>
    suspend fun insertChallenge(reto: Challenger)
    suspend fun updateProgress(id: Long, progress: Int)
    suspend fun getActiveChallenges(uid: String): List<Challenger>
    suspend fun deleteChallenge(challengeId: Long)
}