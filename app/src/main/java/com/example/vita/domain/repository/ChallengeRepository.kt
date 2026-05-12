package com.example.vita.domain.repository

import com.example.vita.domain.model.Challenger

interface ChallengeRepository {
    suspend fun insertChallenge(reto: Challenger)
    suspend fun insertChallenges(retos: List<Challenger>)
    suspend fun updateReto(challenger: Challenger)
    suspend fun getActiveChallenges(uid: String): List<Challenger>
    suspend fun deleteChallenge(challengeId: Long)
    suspend fun getChallengesCreatedToday(uid: String): List<Challenger>

    // ✅ Nuevos métodos para la gestión de expiración
    suspend fun expirarRetosVencidos(uid: String)
    suspend fun getAllChallengesDeHoy(uid: String): List<Challenger>
}