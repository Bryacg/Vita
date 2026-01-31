package com.example.vita.domain.usecase.chat

import com.example.vita.domain.model.ChatMessage
import com.example.vita.domain.repository.ChatRepository
import javax.inject.Inject

class EnviarMensajeChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: ChatMessage): ChatMessage {
        return repository.sendMessage(message)
    }
}