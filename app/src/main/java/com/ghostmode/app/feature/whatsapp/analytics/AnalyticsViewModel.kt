package com.ghostmode.app.feature.whatsapp.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ghostmode.app.data.local.chat.ChatDatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val totalMessages: Int = 0,
    val totalPinned: Int = 0,
    val messagesToday: Int = 0,
    val messagesPerDay: Map<Long, Int> = emptyMap(), // key: start-of-day millis
    val messagesPerConversation: Map<String, Int> = emptyMap()
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val db = ChatDatabaseManager.getInstance(context)
            val dao = db.chatMessageDao()

            // Load all WhatsApp messages once for aggregation (dataset is expected to be small / moderate)
            val allMessages = dao.getAllMessagesOnce()

            val totalMessages = allMessages.size
            val totalPinned = allMessages.count { it.isPinned }

            val now = System.currentTimeMillis()
            val startOfToday = startOfDayMillis(now)
            val endOfToday = startOfToday + 24 * 60 * 60 * 1000L
            val messagesToday = allMessages.count { it.timestamp in startOfToday until endOfToday }

            // Group by day (start-of-day millis)
            val perDay = allMessages.groupingBy { startOfDayMillis(it.timestamp) }.eachCount()

            // Group by conversation name and take top 5
            val perConversationAll = allMessages
                .filter { it.conversationName.isNotBlank() }
                .groupingBy { it.conversationName }
                .eachCount()

            val perConversationTop5 = perConversationAll
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .associate { it.key to it.value }

            _uiState.update {
                it.copy(
                    totalMessages = totalMessages,
                    totalPinned = totalPinned,
                    messagesToday = messagesToday,
                    messagesPerDay = perDay,
                    messagesPerConversation = perConversationTop5
                )
            }
        }
    }

    private fun startOfDayMillis(timestamp: Long): Long {
        val oneDayMillis = 24 * 60 * 60 * 1000L
        return timestamp - (timestamp % oneDayMillis)
    }
}

