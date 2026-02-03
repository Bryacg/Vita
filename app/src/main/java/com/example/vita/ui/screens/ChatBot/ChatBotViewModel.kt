package com.example.vita.ui.screens.ChatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.usecase.chat.EnviarMensajeChatUseCase
import com.example.vita.domain.usecase.chat.ObtenerPromptNutricionalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase,
    private val obtenerPromptNutricionalUseCase: ObtenerPromptNutricionalUseCase // Inyectamos el nuevo UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun enviarMensaje(texto: String) {
        if (texto.isBlank()) return

        val userMsg = ChatMessage(sender = "user", content = texto)

        viewModelScope.launch {
            // 1. Mostrar mensaje del usuario inmediatamente
            _uiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }

            try {
                // 2. CONEXIÓN: Obtenemos el prompt enriquecido con datos de salud y gustos
                val promptCompleto = obtenerPromptNutricionalUseCase(texto)

                // 3. Enviamos a Gemini (usamos el prompt completo pero el usuario solo ve su duda)
                val responseBot = enviarMensajeChatUseCase(ChatMessage(sender = "user", content = promptCompleto))

                // 4. Añadimos respuesta de la IA
                _uiState.update { it.copy(messages = it.messages + responseBot, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}