package com.example.vita.ui.screens.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.usecase.auth.ObtenerSesionUseCase
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
    private val obtenerSesionUseCase: ObtenerSesionUseCase   // reemplaza los 3 repositorios
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInUseCase(email, password).fold(
                onSuccess  = { verificarDatos() },
                onFailure  = { _uiState.update { s ->
                    s.copy(isLoading = false,
                        error = "Credenciales inválidas. Verifica tu correo y contraseña.")
                }}
            )
        }
    }

    fun loginConGoogle(context: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInWithGoogleUseCase(context).fold(
                onSuccess  = { verificarDatos() },
                onFailure  = { e -> _uiState.update { s ->
                    s.copy(isLoading = false, error = e.message ?: "Error con Google.")
                }}
            )
        }
    }

    private suspend fun verificarDatos() {
        try {
            val resultado = obtenerSesionUseCase()
            _uiState.update { it.copy(
                isLoading        = false,
                success          = true,
                navigateToProfile = resultado.necesitaPerfil
            )}
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}

data class LoginUiState(
    val success: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val navigateToProfile: Boolean = false
)