package com.example.vita.data.repository


import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao
) : ChallengeRepository {

    override suspend fun insertChallenge(reto: Challenger) {
        challengeDao.insertChallenge(reto.toEntity())
    }

    // ESTA ES LA FUNCIÓN QUE TE PIDE EL ERROR
    override suspend fun updateProgress(id: Long, progress: Int) {
        challengeDao.updateProgress(id, progress)
    }

    // NUEVO: Implementación para obtener retos activos por ID de usuario
    override suspend fun getActiveChallenges(uid: String): List<Challenger> {
        return challengeDao.getActiveChallenges(uid).map { it.toDomain() }
    }

    // NUEVO: Implementación para eliminar un reto
    override suspend fun deleteChallenge(challengeId: Long) {
        challengeDao.deleteChallenge(challengeId)
    }
}