package com.example.vita.ui.screens.ChatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.usecase.chat.EnviarMensajeChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase
) : ViewModel() {

    // Cambiamos variables sueltas por un solo estado unificado
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Cambiamos el nombre a enviarMensaje (como pide tu UI)
    fun enviarMensaje(texto: String) {
        if (texto.isBlank()) return

        val userMsg = ChatMessage(
            sender = "user",
            content = texto,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // 1. Añadimos el mensaje del usuario y activamos el loading
            _uiState.update { it.copy(
                messages = it.messages + userMsg,
                isLoading = true
            ) }

            try {
                // 2. Llamamos al UseCase
                val responseBot = enviarMensajeChatUseCase(userMsg)

                // 3. Añadimos la respuesta de la IA y quitamos el loading
                _uiState.update { it.copy(
                    messages = it.messages + responseBot,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                // Opcional: podrías añadir un mensaje de error a la lista aquí
            }
        }
    }
}