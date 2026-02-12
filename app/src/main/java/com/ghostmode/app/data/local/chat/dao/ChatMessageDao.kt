package com.ghostmode.app.data.local.chat.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ghostmode.app.data.local.chat.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.IGNORE)
    suspend fun insert(message: ChatMessageEntity): Long

    @Update
    suspend fun update(message: ChatMessageEntity)

    @Delete
    suspend fun delete(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE app = 'whatsapp' ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE app = 'whatsapp' AND isPinned = 1 ORDER BY timestamp DESC")
    fun getPinnedMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE app = 'whatsapp' AND (conversationName LIKE :query OR senderName LIKE :query OR messageBody LIKE :query) ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<ChatMessageEntity>>

    // Used for analytics aggregation; executes on a background dispatcher in ViewModel
    @Query("SELECT * FROM chat_messages WHERE app = 'whatsapp'")
    suspend fun getAllMessagesOnce(): List<ChatMessageEntity>

    @Query("UPDATE chat_messages SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()

    @Query("SELECT * FROM chat_messages WHERE conversationName = :conversationName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMessageForConversation(conversationName: String): ChatMessageEntity?

    @Query("SELECT COUNT(*) FROM chat_messages WHERE conversationName = :conversationName AND messageBody = :messageBody AND ABS(timestamp - :timestamp) <= :windowMillis")
    suspend fun countSimilarMessages(conversationName: String, messageBody: String, timestamp: Long, windowMillis: Long = 60000): Int
}

