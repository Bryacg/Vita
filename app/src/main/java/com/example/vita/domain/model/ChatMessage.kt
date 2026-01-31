package com.example.vita.domain.model

/**
 * Mensaje del chatbot (usuario o asistente).
 */
data class ChatMessage(
    val id: String= java.util.UUID.randomUUID().toString(),
    val sender: String,   // "user" o "bot"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
