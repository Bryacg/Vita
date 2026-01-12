package com.example.vita.data.remote.firebase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.vita.data.mapper.toDomain
import com.example.vita.domain.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {
    private val WEB_CLIENT_ID =
        "1069088296554-jspcbmv26iqiqcfrn0bf2d42uvlaode5.apps.googleusercontent.com"

    val isLoggedInFlow: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toDomain()
                ?: return Result.failure(Exception("No se pudo obtener el usuario"))
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(user: User, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()

            val uid = result.user?.uid
                ?: return Result.failure(Exception("Error al crear el usuario"))

            // ✅ Guarda el nombre real en Firebase Auth.
            // Sin esto displayName queda null y ObtenerSesionUseCase
            // termina guardando "Usuario Vita" en Room en lugar del
            // nombre que el usuario escribió en el formulario.
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("${user.name} ${user.lastName}".trim())
                .build()
            result.user?.updateProfile(profileUpdates)?.await()

            // ✅ Devuelve el User construido desde los datos del formulario
            // (no desde toDomain()) para evitar problemas con la caché
            // de Firebase que podría no reflejar el displayName aún.
            Result.success(user.copy(idUsuario = uid))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<User> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            val idToken: String = when {
                credential is GoogleIdTokenCredential -> credential.idToken

                credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ->
                    GoogleIdTokenCredential.createFrom(credential.data).idToken

                else -> return Result.failure(
                    Exception(
                        "Tipo de credencial no soportado: ${credential.type}. " +
                                "Verifica la configuración de SHA-1 en Firebase Console."
                    )
                )
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user?.toDomain()
                ?: return Result.failure(Exception("Error al obtener datos de Firebase"))

            Result.success(user)

        } catch (e: NoCredentialException) {
            Result.failure(
                Exception(
                    "No hay cuentas de Google disponibles en este dispositivo. " +
                            "Agrega una cuenta en Ajustes → Cuentas."
                )
            )
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Inicio de sesión cancelado"))
        } catch (e: GetCredentialException) {
            Result.failure(
                Exception(
                    "Error de Google Sign-In: ${e.message}. " +
                            "Asegúrate de que el SHA-1 esté registrado en Firebase Console."
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun signOut() = auth.signOut()

    fun getAuthenticatedUserInfo(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val fullName  = firebaseUser.displayName ?: ""
        val emailReal = firebaseUser.email ?: ""
        val nameParts = fullName.trim().split("\\s+".toRegex())
        val firstName = nameParts.getOrNull(0) ?: "Usuario"
        val lastName  = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "Vita"

        return User(
            idUsuario    = firebaseUser.uid,
            email        = emailReal,
            name         = firstName,
            lastName     = lastName,
            currentLevel = 1,
            currentXp    = 0
        )
    }
}