package com.ghostmode.app.data.local.chat

import android.content.Context
import androidx.room.Room
import com.ghostmode.app.data.local.chat.database.ChatDatabase

object ChatDatabaseManager {
    @Volatile
    private var instance: ChatDatabase? = null

    fun getInstance(context: Context): ChatDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                ChatDatabase::class.java,
                ChatDatabase.DB_NAME
            )
                // If schema identity changes between installs, recreate the DB instead of crashing.
                // This does NOT change the schema; it just allows Room to clear and rebuild tables.
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
