package com.example.vita.ui.screens.CreateAcount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.User
import com.example.vita.domain.usecase.auth.ObtenerSesionUseCase
import com.example.vita.domain.usecase.auth.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val registerUseCase: RegisterUserUseCase,
    private val obtenerSesionUseCase: ObtenerSesionUseCase  // salva user en Room
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun crearCuenta(name: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val userToRegister = User(
                idUsuario    = "",
                email        = email,
                name         = name,
                lastName     = lastName,
                currentLevel = 1,
                currentXp    = 0
            )

            registerUseCase(userToRegister, password).fold(
                onSuccess = {
                    // Crítico: guarda UserEntity en Room ANTES de que
                    // HomeViewModel intente insertar registros relacionados.
                    // Esto evita el FK constraint (SQLITE error 787).
                    try {
                        val sesion = obtenerSesionUseCase()
                        _uiState.update {
                            it.copy(
                                isLoading        = false,
                                success          = true,
                                navigateToProfile = sesion.necesitaPerfil,
                                error            = null
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(isLoading = false, error = e.message)
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success   = false,
                            error     = mensajeDeError(e.message)
                        )
                    }
                }
            )
        }
    }

    private fun mensajeDeError(raw: String?): String = when {
        raw == null                          -> "Error desconocido"
        raw.contains("email")                -> "Ese correo ya está registrado"
        raw.contains("password")             -> "La contraseña debe tener al menos 6 caracteres"
        raw.contains("network")              -> "Sin conexión a internet"
        else                                 -> "Error al crear la cuenta"
    }
}

data class CreateAccountUiState(
    val success: Boolean           = false,
    val navigateToProfile: Boolean = false,
    val error: String?             = null,
    val isLoading: Boolean         = false
)