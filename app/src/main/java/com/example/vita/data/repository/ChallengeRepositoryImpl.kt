package com.example.vita.data.repository

import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.mapper.toDomain

import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val dao: ChallengeDao
) : ChallengeRepository {

    override suspend fun getActiveChallenges(uid: String): List<Challenger> = withContext(Dispatchers.IO) {
        dao.getActiveChallenges(uid,).map { it.toDomain() }
    }


    override suspend fun updateProgress(challengeId: Long, amount: Int) = withContext(Dispatchers.IO) {
        dao.updateProgress(challengeId, amount)
    }

    override suspend fun deleteChallenge(challengeId: Long) = withContext(Dispatchers.IO) {
        dao.deleteChallenge(challengeId)
    }
}