package com.example.vita.ui.screens.ChatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.usecase.chat.EnviarMensajeChatUseCase
import com.example.vita.domain.usecase.chat.ObtenerPromptNutricionalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ ChatUiState vive junto a su ViewModel, no en la Screen
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase,
    private val obtenerPromptNutricionalUseCase: ObtenerPromptNutricionalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun enviarMensaje(texto: String) {
        if (texto.isBlank()) return

        val userMsg = ChatMessage(sender = "user", content = texto)

        viewModelScope.launch {
            _uiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }

            try {
                val promptCompleto   = obtenerPromptNutricionalUseCase(texto)
                val responseBot      = enviarMensajeChatUseCase(
                    ChatMessage(sender = "user", content = promptCompleto)
                )
                _uiState.update {
                    it.copy(messages = it.messages + responseBot, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}