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
        return try {
            firebaseAuthDataSource.signInWithGoogle(context)
        } catch (e: com.google.android.gms.common.api.ApiException) {
            // El código 10 o 12500 significa error de configuración (SHA-1 o Client ID)
            Result.failure(Exception("Error de Google Status: ${e.statusCode}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return firebaseAuthDataSource.login(email, password)
    }

    override fun signOut() {
        firebaseAuthDataSource.signOut()
    }
    

    override fun getCurrentUserId(): String? {
        return firebaseAuthDataSource.getCurrentUserId()
    }
    override fun getCurrentUser(): User? {
        // Obtenemos el ID actual
        val uid = firebaseAuthDataSource.getCurrentUserId() ?: return null

        // Debes asegurarte de que tu DataSource pueda acceder al FirebaseUser actual
        // Si tu DataSource tiene una instancia de FirebaseAuth, puedes extraer:
        // val fbUser = firebaseAuth.currentUser

        // Por ahora, devolvemos la estructura necesaria.
        // Nota: Asegúrate de que firebaseAuthDataSource exponga estos datos.
        return firebaseAuthDataSource.getAuthenticatedUserInfo()
    }
}
