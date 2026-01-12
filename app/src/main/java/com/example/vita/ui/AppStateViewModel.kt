package com.example.vita.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppStateViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado de sesión expuesto como StateFlow
    val isLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun signOut() {
        authRepository.signOut()
    }

    fun getCurrentUserId(): String? = authRepository.getCurrentUserId()
}
