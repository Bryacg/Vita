package com.example.vita.ui.screens.CreateAcount
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.auth.RegisterUseCase

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun crearCuenta(email: String, password: String) {
        viewModelScope.launch {
            val result = registerUseCase(email, password)
            _uiState.update { it.copy(success = result.success, error = result.error) }
        }
    }
}

data class CreateAccountUiState(val success: Boolean = false, val error: String? = null)