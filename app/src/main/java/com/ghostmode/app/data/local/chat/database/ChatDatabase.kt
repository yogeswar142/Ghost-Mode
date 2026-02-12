package com.ghostmode.app.data.local.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ghostmode.app.data.local.chat.dao.ChatMessageDao
import com.ghostmode.app.data.local.chat.entity.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        const val DB_NAME: String = "chat_db"
    }
}

