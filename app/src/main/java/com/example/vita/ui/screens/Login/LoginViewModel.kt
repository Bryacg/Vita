package com.example.vita.ui.screens.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.auth.SignInUseCase
import com.example.vita.domain.usecase.auth.SignInWithGoogleUseCase

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = signInUseCase(email, password)
            _uiState.update {
                if (result != null) {
                    it.copy(success = true, error = null)
                } else {
                    it.copy(success = false, error = "Credenciales inválidas")
                }
            }

        }
    }

    fun loginConGoogle(token: String) {
        viewModelScope.launch {
            val result = signInWithGoogleUseCase(token)
            _uiState.update {
                if (result != null) {
                    it.copy(success = true, error = null)
                } else {
                    it.copy(success = false, error = "Error al iniciar sesión con Google")
                }
            }


        }
    }
}

data class LoginUiState(val success: Boolean = false, val error: String? = null)