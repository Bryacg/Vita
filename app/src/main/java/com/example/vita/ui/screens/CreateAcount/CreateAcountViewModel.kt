package com.example.vita.ui.screens.CreateAcount
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.usecase.auth.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.model.User // Asegúrate de importar tu modelo User


@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val registerUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    // Ahora la función recibe los 4 parámetros necesarios
    fun crearCuenta(name: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Creamos el usuario con los datos reales de la vista
            val userToRegister = User(
                idUsuario = "",
                email = email,
                name = name,
                lastName = lastName,
                currentLevel = 1,
                currentXp = 0
            )

            // 2. Ahora sí el UseCase recibe el objeto completo
            val result = registerUseCase(userToRegister, password)

            _uiState.update { state ->
                result.fold(
                    onSuccess = { state.copy(success = true, error = null, isLoading = false) },
                    onFailure = { state.copy(success = false, error = it.message, isLoading = false) }
                )
            }
        }
    }
}

data class CreateAccountUiState(
    val success: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)