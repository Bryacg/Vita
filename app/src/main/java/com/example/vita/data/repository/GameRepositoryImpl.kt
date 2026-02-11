package com.example.vita.data.repository

import com.example.vita.data.local.dao.GameDao
import com.example.vita.data.local.dao.UserDao
import com.example.vita.data.local.entities.GameEntity
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.GameResult
import com.example.vita.domain.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val userDao: UserDao
) : GameRepository {

    override suspend fun saveGameResult(result: GameResult): GameResult {
        // ERROR SOLUCIONADO: Cambiamos insertGameResult por insertResult
        gameDao.insertResult(result.toEntity())
        return result
    }

    override suspend fun getResultsByUser(uid: String): List<GameResult> {

        return gameDao.getResultsByUser(uid).map { entity: GameEntity ->
            entity.toDomain()
        }
    }

    override suspend fun addXpToUser(uid: String, xp: Int): Int {
        // Obtenemos el usuario actual para calcular el nuevo nivel/xp
        val user = userDao.getUserById(uid) ?: return 0

        val nuevaXp = user.currentXp + xp
        val nuevoNivel = user.currentLevel // Aquí podrías añadir lógica de subir nivel

        // ERROR SOLUCIONADO: Usamos el nombre exacto de tu UserDao
        userDao.updateUserXpAndLevel(uid, nuevaXp, nuevoNivel)

        return nuevaXp
    }
}