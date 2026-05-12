package com.example.vita.ui.screens.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import com.example.vita.domain.usecase.auth.SignInWithEmailUseCase
import com.example.vita.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = signInUseCase(email, password)

            // ✅ Usando fold() para manejar correctamente éxito y error
            result.fold(
                onSuccess = { verificarDatosUsuario() },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Credenciales inválidas. Verifica tu correo y contraseña."
                        )
                    }
                }
            )
        }
    }

    fun loginConGoogle(context: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = signInWithGoogleUseCase(context)

            // ✅ Usando fold()
            result.fold(
                onSuccess = { verificarDatosUsuario() },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al iniciar sesión con Google."
                        )
                    }
                }
            )
        }
    }

    private suspend fun verificarDatosUsuario() {
        val uid = authRepository.getCurrentUserId() ?: run {
            _uiState.update { it.copy(isLoading = false, error = "No se pudo obtener el usuario.") }
            return
        }

        val userFromAuth = authRepository.getCurrentUser()
        val existingUser = userRepository.getUserById(uid)

        if (existingUser == null) {
            val newUser = User(
                idUsuario = uid,
                email = userFromAuth?.email ?: "",
                name = userFromAuth?.name ?: "Usuario",
                lastName = userFromAuth?.lastName ?: "Vita",
                currentLevel = 1,
                currentXp = 0
            )
            userRepository.saveUser(newUser)
        }

        val profile = profileRepository.getProfileByUserId(uid)

        _uiState.update {
            it.copy(
                isLoading = false,
                success = true,
                navigateToProfile = (profile == null || profile.weight == 0f)
            )
        }
    }
}

data class LoginUiState(
    val success: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val navigateToProfile: Boolean = false
)