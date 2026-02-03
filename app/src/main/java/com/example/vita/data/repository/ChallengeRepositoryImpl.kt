package com.example.vita.data.repository

import com.example.vita.data.local.dao.ChallengeDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Challenger
import com.example.vita.domain.repository.ChallengeRepository
import com.example.vita.di.RetosApi
import com.example.vita.domain.usecase.retos.GenerarRetosIAUseCase
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import javax.inject.Inject
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao,
    private val generarRetosIAUseCase: GenerarRetosIAUseCase
) : ChallengeRepository {

    // 1. Usamos '=' para que el bloque withContext sea el retorno (Soluciona 'Missing return statement')
    // 2. Asegúrate de que en la Interfaz ChallengeRepository exista esta misma firma (Soluciona 'overrides nothing')
    override suspend fun generarYGuardarRetos(uid: String, nombre: String): List<Challenger> = withContext(Dispatchers.IO) {
        try {
            val retosIA = generarRetosIAUseCase(uid, nombre)

            if (retosIA.isNotEmpty()) {
                // Completamos los datos para que no se guarden vacíos
                val retosCompletos = retosIA.map { reto ->
                    reto.copy(
                        userId = uid,
                        status = "ACTIVO",
                        currentValue = 0,
                        deadline = System.currentTimeMillis() + 86400000
                    )
                }

                // Guardamos en Room
                challengeDao.insertChallenges(retosCompletos.map { it.toEntity() })
                Log.d("VITA_LOG", "Retos guardados exitosamente: ${retosCompletos.size}")

                // Devolvemos la lista completa
                retosCompletos
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("VITA_LOG", "Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun insertChallenge(reto: Challenger) = withContext(Dispatchers.IO) {
        challengeDao.insertChallenges(listOf(reto.toEntity()))
    }

    override suspend fun updateProgress(id: Long, progress: Int) = withContext(Dispatchers.IO) {
        challengeDao.updateProgress(id, progress)
    }

    override suspend fun getActiveChallenges(uid: String): List<Challenger> = withContext(Dispatchers.IO) {
        challengeDao.getActiveChallenges(uid).map { it.toDomain() }
    }

    override suspend fun deleteChallenge(challengeId: Long) = withContext(Dispatchers.IO) {
        challengeDao.deleteChallenge(challengeId)
    }
}