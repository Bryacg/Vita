package com.example.vita.domain.repository

import com.example.vita.domain.model.GameResult

interface GamesRepository {
    suspend fun insertResult(result: GameResult)
    suspend fun getResultsByUser(uid: String): List<GameResult>
    suspend fun getTotalXpFromGames(uid: String): Int
}
