package com.example.vita.data.repository

import com.example.vita.data.local.dao.UserDao // Asegúrate de tener este DAO
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    /**
     * Devuelve un flujo constante del usuario.
     * Gracias a Room, cada vez que el XP cambie, la UI se actualizará automáticamente.
     */
    override fun getUserStream(uid: String): Flow<User?> {
        return userDao.getUserStream(uid).map { entity ->
            entity?.toDomain()
        }
    }

    /**
     * Actualiza específicamente el XP y el Nivel.
     * Esta función es la que llamará el HomeViewModel al ganar 80, 400 o 170 XP.
     */
    override suspend fun updateUserXpAndLevel(uid: String, newXp: Int, newLevel: Int) {
        withContext(Dispatchers.IO) {
            userDao.updateUserXpAndLevel(uid, newXp, newLevel)
        }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)?.toDomain()
    }

    override suspend fun deleteUserData() {
        // Es más eficiente llamar a un método deleteAll() en el DAO si lo tienes
        userDao.deleteAllUsers()
    }
}