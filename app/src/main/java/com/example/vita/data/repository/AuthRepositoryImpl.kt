package com.example.vita.data.repository

import com.example.vita.data.remote.firebase.FirebaseAuthDataSource
import com.example.vita.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override val isUserLoggedInFlow: Flow<Boolean>
        get() = firebaseAuthDataSource.isLoggedInFlow

    override fun signOut() {
        firebaseAuthDataSource.signOut()
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuthDataSource.getCurrentUserId()
    }
}
