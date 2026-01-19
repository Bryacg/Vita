package com.example.vita.domain.repository

import android.content.Context
import com.example.vita.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserLoggedInFlow: Flow<Boolean>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>

    // AGREGA ESTA LÍNEA PARA SOLUCIONAR EL ERROR:
    suspend fun signInWithGoogle(context: Context): Result<User>

    fun signOut()
    fun getCurrentUserId(): String?
}