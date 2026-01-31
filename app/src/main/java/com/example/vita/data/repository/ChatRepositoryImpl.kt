package com.example.vita.data.repository

import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// En la capa de DATA
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : ChatRepository {

    override suspend fun sendMessage(message: ChatMessage): ChatMessage {
        return try {
            // Enviamos el contenido del mensaje a Gemini
            val response = generativeModel.generateContent(message.content)

            ChatMessage(
                sender = "bot",
                content = response.text ?: "No pude procesar esa información.",
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            ChatMessage(
                sender = "bot",
                content = "Error de conexión: ${e.localizedMessage}",
                timestamp = System.currentTimeMillis()
            )
        }
    }

    override suspend fun getConversationHistory(uid: String): List<ChatMessage> {
        // Aquí podrías retornar mensajes desde tu base de datos Room si los guardas
        return emptyList()
    }
}