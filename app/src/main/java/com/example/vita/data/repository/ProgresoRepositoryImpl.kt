package com.example.vita.data.repository

import com.example.vita.data.local.dao.ProgressDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Progress
import com.example.vita.domain.repository.ProgresoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProgresoRepositoryImpl @Inject constructor(
    private val dao: ProgressDao
) : ProgresoRepository {

    // SOLUCIÓN AL ERROR: Implementación del Stream reactivo
    override fun getProgresoStream(uid: String): Flow<Progress?> {
        return dao.getProgresoStream(uid).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getProgreso(uid: String): Progress? = withContext(Dispatchers.IO) {
        dao.getProgressByUser(uid)?.toDomain()
    }

    override suspend fun insertarProgreso(progreso: Progress) = withContext(Dispatchers.IO) {
        dao.insertProgress(progreso.toEntity())
    }

    override suspend fun agregarXp(uid: String, xp: Int) = withContext(Dispatchers.IO) {
        dao.addXp(uid, xp)
    }

    override suspend fun actualizarNivel(uid: String, nivel: Int) = withContext(Dispatchers.IO) {
        dao.updateLevel(uid, nivel)
    }

    override suspend fun actualizarBmi(uid: String, bmi: Double) = withContext(Dispatchers.IO) {
        dao.updateBmi(uid, bmi)
    }

    override suspend fun actualizarRacha(uid: String, dias: Int) = withContext(Dispatchers.IO) {
        dao.updateStreak(uid, dias)
    }

    override suspend fun resetearRacha(uid: String) = withContext(Dispatchers.IO) {
        dao.resetStreak(uid)
    }
    override suspend fun getProgresoPorFecha(uid: String, fecha: Long): Progress? = withContext(Dispatchers.IO) {
        dao.getProgressByDate(uid, fecha)?.toDomain()
    }

    override suspend fun getTotalXpDeSiempre(uid: String): Int = withContext(Dispatchers.IO) {
        dao.getTotalXp(uid) ?: 0
    }

    override suspend fun agregarXpHoy(uid: String, xp: Int, fecha: Long) = withContext(Dispatchers.IO) {
        dao.addXpHoy(uid, xp, fecha)
    }
}