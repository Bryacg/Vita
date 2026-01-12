package com.example.vita.domain.repository
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserLoggedInFlow: Flow<Boolean>
    fun signOut()
    fun getCurrentUserId(): String?
}
