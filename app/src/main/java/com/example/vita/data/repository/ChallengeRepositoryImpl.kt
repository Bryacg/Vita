package com.example.vita.data.repository

import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
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

    override suspend fun getActiveChallenges(uid: String): List<Challenger> = withContext(Dispatchers.IO) {
        challengeDao.getActiveChallenges(uid).map { it.toDomain() }
    }

    override suspend fun deleteChallenge(challengeId: Long) = withContext(Dispatchers.IO) {
        challengeDao.deleteChallenge(challengeId)
    }

    // ✅ Calcula inicio y fin del día actual y consulta Room
    override suspend fun getChallengesCreatedToday(uid: String): List<Challenger> =
        withContext(Dispatchers.IO) {
            val startOfDay = getStartOfDayMillis()
            val endOfDay = startOfDay + 86_400_000L // +24 horas

            challengeDao.getChallengesCreatedToday(uid, startOfDay, endOfDay)
                .map { it.toDomain() }
        }

    private fun getStartOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}