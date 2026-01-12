package com.example.vita.data.repository

import com.example.vita.data.local.dao.GameResultDao
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.GamesRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val dao: GameResultDao
) : GamesRepository {

    override suspend fun insertResult(result: GameResult) = withContext(Dispatchers.IO) {
        dao.insertResult(result.toEntity())
    }

    override suspend fun getResultsByUser(uid: String): List<GameResult> = withContext(Dispatchers.IO) {
        dao.getResultsByUser(uid).map { it.toDomain() }
    }

    override suspend fun getTotalXpFromGames(uid: String): Int = withContext(Dispatchers.IO) {
        dao.getTotalXpFromGames(uid) as Int
    }
}
