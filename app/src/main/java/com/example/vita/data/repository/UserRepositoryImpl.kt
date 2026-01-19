package com.example.vita.data.repository

import com.example.vita.data.local.dao.UserDao // Asegúrate de tener este DAO
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun saveUser(user: User) {
        // Aquí conviertes tu modelo de dominio a entidad de Room y guardas
        userDao.insertUser(user.toEntity())
    }

    override suspend fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)?.toDomain()
    }

    override suspend fun deleteUserData() {
        // Opción segura: obtener todos y borrarlos o usar el método deleteAll
        val users = userDao.getAllUsers()
        users.forEach { userEntity ->
            userDao.deleteUser(userEntity)
        }
}
}