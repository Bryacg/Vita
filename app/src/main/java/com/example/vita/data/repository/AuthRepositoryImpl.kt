package com.example.vita.data.repository

import android.content.Context
import com.example.vita.data.remote.firebase.FirebaseAuthDataSource
import com.example.vita.domain.model.User
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

    override suspend fun register(user: User, password: String): Result<User> {
        return firebaseAuthDataSource.register(user, password)
    }

    override suspend fun signInWithGoogle(context: Context): Result<User> {
        return firebaseAuthDataSource.signInWithGoogle(context)
    } // <-- ESTA ES LA LLAVE QUE FALTABA

    override suspend fun login(email: String, password: String): Result<User> {
        return firebaseAuthDataSource.login(email, password)
    }

    override fun signOut() {
        firebaseAuthDataSource.signOut()
    }
    

    override fun getCurrentUserId(): String? {
        return firebaseAuthDataSource.getCurrentUserId()
    }
}