package com.example.vita.ui.screens.ChatBot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.vita.domain.usecase.chat.EnviarMensajeChatUseCase
import java.util.UUID


@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val enviarMensajeChatUseCase: EnviarMensajeChatUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun enviarMensaje(userId: String, mensaje: String) {
        viewModelScope.launch {
            val mensajeChat = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = userId,
                content = mensaje,
                timestamp = System.currentTimeMillis()
            )
            val respuesta = enviarMensajeChatUseCase(mensajeChat)
            _uiState.update { it.copy(messages = it.messages + respuesta) }
        }
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList()
)
data class ProfileUiState(
    val user: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
