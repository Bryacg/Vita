package com.example.vita.ui.screens.Login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = signInUseCase(email, password)
            _uiState.update {
                if (result != null) {
                    it.copy(success = true, error = null, isLoading = false)
                } else {
                    it.copy(success = false, error = "Credenciales inválidas", isLoading = false)
                }
            }
        }
    }

    fun loginConGoogle(context: Context) {
        // Si ya está cargando, ignoramos el clic para evitar la doble actividad
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = signInWithGoogleUseCase(context)

                result.fold(
                    onSuccess = { /* ... */ },
                    onFailure = { error ->
                        val friendlyMessage = when {
                            error.message?.contains("28433") == true ->
                                "No se encontró una cuenta vinculada. Por favor, selecciona una cuenta de Google manualmente."
                            error.message?.contains("cancel") == true ->
                                "Inicio de sesión cancelado."
                            else -> "Error al conectar con Google. Verifica tu conexión."
                        }
                        _uiState.update { it.copy(error = friendlyMessage, isLoading = false) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
} // Cierre de la clase ViewModel

data class LoginUiState(
    val success: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)