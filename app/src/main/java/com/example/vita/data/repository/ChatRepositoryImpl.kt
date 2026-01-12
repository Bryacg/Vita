package com.example.vita.data.repository

import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    // Aquí inyectas ChatRemoteDataSource (OpenAI/Gemini)
) : ChatRepository {

    override suspend fun sendMessage(message: ChatMessage): ChatMessage {
        // Lógica con ChatRemoteDataSource
        TODO()
    }

    override suspend fun getConversationHistory(uid: String): List<ChatMessage> {
        // Podrías persistir en Room o Firestore
        TODO()
    }
}
