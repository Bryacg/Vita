package com.example.vita.domain.repository

import com.example.vita.domain.model.Challenger

interface ChallengeRepository {
    suspend fun insertChallenge(reto: Challenger)
    suspend fun insertChallenges(retos: List<Challenger>)
    suspend fun updateReto(challenger: Challenger)
    suspend fun getActiveChallenges(uid: String): List<Challenger>
    suspend fun deleteChallenge(challengeId: Long)

    // Diarios creados hoy
    suspend fun getDailyChallengesDeHoy(uid: String): List<Challenger>

    // Semanales creados esta semana (lunes–domingo)
    suspend fun getSemanalesEstaSemana(uid: String): List<Challenger>

    // Diarios de hoy + semanales de esta semana (para la pantalla)
    suspend fun getAllChallengesParaHoy(uid: String): List<Challenger>

    // Expira los retos cuyo deadline pasó
    suspend fun expirarRetosVencidos(uid: String)

    // Métodos legacy — delegan a los nuevos
    suspend fun getChallengesCreatedToday(uid: String): List<Challenger>
    suspend fun getAllChallengesDeHoy(uid: String): List<Challenger>
}