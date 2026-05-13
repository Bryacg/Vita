package com.example.vita.data.repository

import com.example.vita.data.local.dao.ChatMessageDao
import com.example.vita.data.local.entities.ChatMessageEntity
import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.repository.ChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val chatMessageDao: ChatMessageDao
) : ChatRepository {

    override suspend fun sendMessage(message: ChatMessage): ChatMessage {
        return withContext(Dispatchers.IO) {
            try {
                // Guardamos el mensaje del usuario
                chatMessageDao.insertMessage(message.toEntity())

                val response = generativeModel.generateContent(message.content)
                val botMessage = ChatMessage(
                    sender    = "bot",
                    content   = response.text ?: "No pude procesar esa información.",
                    timestamp = System.currentTimeMillis()
                )

                // Guardamos la respuesta del bot
                chatMessageDao.insertMessage(botMessage.toEntity())
                botMessage

            } catch (e: Exception) {
                ChatMessage(
                    sender    = "bot",
                    content   = "Error de conexión: ${e.localizedMessage}",
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    override suspend fun getConversationHistory(uid: String): List<ChatMessage> =
        withContext(Dispatchers.IO) {
            chatMessageDao.getMessagesByUser(uid).map { it.toDomain() }
        }

    private fun ChatMessage.toEntity() = ChatMessageEntity(
        id        = id,
        userId    = "local",
        sender    = sender,
        content   = content,
        timestamp = timestamp
    )

    private fun ChatMessageEntity.toDomain() = ChatMessage(
        id        = id,
        sender    = sender,
        content   = content,
        timestamp = timestamp
    )
}