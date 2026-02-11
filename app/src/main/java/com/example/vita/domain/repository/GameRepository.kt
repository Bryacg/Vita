package com.example.vita.domain.repository

import com.example.vita.domain.model.GameResult

interface GameRepository {

    suspend fun saveGameResult(result: GameResult): GameResult


    suspend fun getResultsByUser(uid: String): List<GameResult>


    suspend fun addXpToUser(uid: String, xp: Int): Int// Añadimos el parámetro 'xp'
}