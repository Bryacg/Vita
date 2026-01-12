package com.example.vita.data.mapper

import com.example.vita.domain.model.ChatMessage

// En este caso no hay Entity porque el chat se maneja remoto (OpenAI/Gemini).
// Si decides persistir mensajes en Room, crearías ChatMessageEntity y mapearías aquí.

fun ChatMessage.toRemoteRequest(): Map<String, Any> = mapOf(
    "id" to id,
    "sender" to sender,
    "content" to content,
    "timestamp" to timestamp
)