package com.example.vita.data.repository

import com.example.vita.data.local.dao.GameResultDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.GamesRepository
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao
) : GamesRepository {

    // Cambia el nombre aquí para que coincida con la interfaz
    override suspend fun saveGameResult(result: GameResult): GameResult {
        val entity = result.toEntity()
        val generatedId = gameResultDao.insertGameResult(entity)
        return result.copy(id = generatedId)
    }

    override suspend fun getResultsByUser(uid: String): List<GameResult> {
        return gameResultDao.getResultsByUserId(uid).map { it.toDomain() }
    }

    override suspend fun getTotalXpFromGames(uid: String): Int {
        // El operador ?: 0 asegura que si no hay registros, devuelva 0 XP
        return gameResultDao.getSumXpByUserId(uid) ?: 0
    }
}