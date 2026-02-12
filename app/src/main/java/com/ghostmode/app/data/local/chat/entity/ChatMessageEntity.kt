package com.ghostmode.app.data.local.chat.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["conversationName", "senderName", "messageBody", "timestamp"], unique = true)]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val app: String = "whatsapp",
    val conversationName: String,
    val senderName: String? = null,
    val messageBody: String,
    val timestamp: Long,
    val isPinned: Boolean = false
)

