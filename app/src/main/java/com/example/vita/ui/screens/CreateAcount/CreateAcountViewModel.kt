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
    private val obtenerSesionUseCase: ObtenerSesionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun crearCuenta(name: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // El User contiene el nombre real del formulario
            val userDelFormulario = User(
                idUsuario    = "",   // Firebase asigna el UID real
                email        = email,
                name         = name.trim(),
                lastName     = lastName.trim(),
                currentLevel = 1,
                currentXp    = 0
            )

            registerUseCase(userDelFormulario, password).fold(
                onSuccess = { usuarioRegistrado ->
                    // usuarioRegistrado ya tiene el UID real y el nombre del formulario
                    try {
                        // ✅ Pasamos el User con el nombre real al UseCase.
                        // ObtenerSesionUseCase lo guarda en Room directamente
                        // sin depender del displayName de Firebase.
                        val sesion = obtenerSesionUseCase(usuarioRegistrado)

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
        raw == null                 -> "Error desconocido"
        raw.contains("email")       -> "Ese correo ya está registrado"
        raw.contains("password")    -> "La contraseña debe tener al menos 6 caracteres"
        raw.contains("network")     -> "Sin conexión a internet"
        else                        -> "Error al crear la cuenta"
    }
}

data class CreateAccountUiState(
    val success: Boolean           = false,
    val navigateToProfile: Boolean = false,
    val error: String?             = null,
    val isLoading: Boolean         = false
)