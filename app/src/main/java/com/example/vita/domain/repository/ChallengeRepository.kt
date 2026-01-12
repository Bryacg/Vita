package com.example.vita.domain.repository

import com.example.vita.domain.model.Challenger

interface ChallengeRepository {
    suspend fun getActiveChallenges(uid: String): List<Challenger>

    suspend fun updateProgress(challengeId: Long, amount: Int)
    suspend fun deleteChallenge(challengeId: Long)
}