package com.example.vita.domain.repository

import com.example.vita.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User)
    suspend fun getUserById(uid: String): User?
    suspend fun deleteUserData()
}