package com.example.vita.data.local.dao

import androidx.room.*
import com.example.vita.data.local.entities.ChatMessageEntity

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_message WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getMessagesByUser(userId: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_message WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}