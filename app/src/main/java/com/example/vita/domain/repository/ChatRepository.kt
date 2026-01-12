package com.example.vita.domain.repository

import com.example.vita.domain.model.ChatMessage

interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage): ChatMessage
    suspend fun getConversationHistory(uid: String): List<ChatMessage>
}
