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

    /**
     * null  → Firebase todavía está verificando la sesión (mostrar splash)
     * true  → usuario autenticado
     * false → usuario no autenticado
     */
    val isLoggedIn: StateFlow<Boolean?> = authRepository.isUserLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null // ✅ null mientras Firebase verifica
        )

    fun signOut() {
        authRepository.signOut()
    }

    fun getCurrentUserId(): String? = authRepository.getCurrentUserId()
}