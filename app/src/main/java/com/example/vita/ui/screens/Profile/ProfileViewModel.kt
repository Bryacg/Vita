package com.example.vita.ui.screens.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.progreso.ActualizarBmiUseCase

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val actualizarBmiUseCase: ActualizarBmiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun actualizarBmi(userId: String, bmi: Double) {
        viewModelScope.launch {
            actualizarBmiUseCase(userId, bmi)
            _uiState.update { it.copy(bmi = bmi) }
        }
    }
}

data class ProfileUiState(val bmi: Double? = null)