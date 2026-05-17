package com.example.vita.data.repository

import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.vita.core.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao
) : ChallengeRepository {

    override suspend fun insertChallenge(reto: Challenger) = withContext(Dispatchers.IO) {
        challengeDao.insertChallenges(listOf(reto.toEntity()))
    }

    override suspend fun insertChallenges(retos: List<Challenger>) = withContext(Dispatchers.IO) {
        challengeDao.insertChallenges(retos.map { it.toEntity() })
    }

    override suspend fun updateReto(challenger: Challenger) = withContext(Dispatchers.IO) {
        challengeDao.updateChallenger(challenger.toEntity())
    }

    override suspend fun getActiveChallenges(uid: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            challengeDao.getActiveChallenges(uid).map { it.toDomain() }
        }

    override suspend fun deleteChallenge(challengeId: Long) = withContext(Dispatchers.IO) {
        challengeDao.deleteChallenge(challengeId)
    }

    // ─── Nuevos métodos ────────────────────────────────────────────────────

    override suspend fun getDailyChallengesDeHoy(uid: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            challengeDao.getDailyChallengesDeHoy(
                uid        = uid,
                startOfDay = DateTimeUtils.getTodayMillis(),
                endOfDay   = DateTimeUtils.getTodayEndMillis()
            ).map { it.toDomain() }
        }

    override suspend fun getSemanalesEstaSemana(uid: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            challengeDao.getSemanalesEstaSemana(
                uid          = uid,
                startOfWeek  = DateTimeUtils.getMondayStartMillis(),
                endOfWeek    = DateTimeUtils.getThisSundayEndMillis()
            ).map { it.toDomain() }
        }

    override suspend fun getAllChallengesParaHoy(uid: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            challengeDao.getAllChallengesParaHoy(
                uid         = uid,
                startOfDay  = DateTimeUtils.getTodayMillis(),
                endOfDay    = DateTimeUtils.getTodayEndMillis(),
                startOfWeek = DateTimeUtils.getMondayStartMillis(),
                endOfWeek   = DateTimeUtils.getThisSundayEndMillis()
            ).map { it.toDomain() }
        }

    override suspend fun expirarRetosVencidos(uid: String) = withContext(Dispatchers.IO) {
        challengeDao.expirarRetosVencidos(uid, System.currentTimeMillis())
    }

    // ─── Legacy ────────────────────────────────────────────────────────────

    override suspend fun getChallengesCreatedToday(uid: String): List<Challenger> =
        getDailyChallengesDeHoy(uid)

    override suspend fun getAllChallengesDeHoy(uid: String): List<Challenger> =
        getAllChallengesParaHoy(uid)
}