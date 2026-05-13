package com.example.vita.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val sender: String,
    val content: String,
    val timestamp: Long
)