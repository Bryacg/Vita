package com.example.vita.domain.repository

import com.example.vita.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Agrega estas líneas exactamente así
    fun getUserStream(uid: String): kotlinx.coroutines.flow.Flow<User?>
    suspend fun updateUserXpAndLevel(uid: String, newXp: Int, newLevel: Int)

    // Tus funciones actuales
    suspend fun saveUser(user: User)
    suspend fun getUserById(uid: String): User?
    suspend fun deleteUserData()
}