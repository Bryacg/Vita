package com.example.vita.ui.screens.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.User
import com.example.vita.domain.repository.AuthRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.auth.SignInWithEmailUseCase
import com.example.vita.domain.usecase.auth.SignInWithGoogleUseCase

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val userRepository: UserRepository,      // Inyectado
    private val profileRepository: ProfileRepository, // Inyectado
    private val authRepository: AuthRepository        // Inyectado para obtener el UID
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = signInUseCase(email, password)
            if (result != null) {
                // Si el login es exitoso, procesamos los datos
                verificarDatosUsuario()
            } else {
                _uiState.update { it.copy(success = false, error = "Credenciales inválidas", isLoading = false) }
            }
        }
    }

    fun loginConGoogle(context: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = signInWithGoogleUseCase(context)

            result.fold(
                onSuccess = {
                    // Si Google tiene éxito, procesamos los datos
                    verificarDatosUsuario()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private suspend fun verificarDatosUsuario() {
        val uid = authRepository.getCurrentUserId() ?: return

        // Obtenemos los datos ya mapeados desde Firebase
        val userFromAuth = authRepository.getCurrentUser()

        // Verificamos si ya existe en la base de datos local (Room)
        val existingUser = userRepository.getUserById(uid)

        if (existingUser == null) {
            // Creamos el nuevo usuario para Room con los datos reales obtenidos
            val newUser = User(
                idUsuario = uid,
                email = userFromAuth?.email ?: "", // Usamos 'email' de tu clase User
                name = userFromAuth?.name ?: "Usuario", // Usamos 'name' de tu clase User
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
    val navigateToProfile: Boolean = false // Nueva bandera
)