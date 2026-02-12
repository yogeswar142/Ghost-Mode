package com.ghostmode.app.core.notification

import android.app.Notification
import android.os.Bundle
import android.util.Log
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.ghostmode.app.data.local.chat.ChatDatabaseManager
import com.ghostmode.app.data.local.chat.entity.ChatMessageEntity
import com.ghostmode.app.data.local.chat.dao.ChatMessageDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageNotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        Log.d("MessageNLS", "onNotificationPosted from package=${sbn.packageName}")

        // Only capture WhatsApp notifications
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") {
            Log.d("MessageNLS", "Ignoring non-WhatsApp notification: ${sbn.packageName}")
            return
        }

        val notification = sbn.notification ?: run {
            Log.d("MessageNLS", "Notification is null, skipping")
            return
        }
        val extras = notification.extras ?: Bundle.EMPTY

        // Ignore ongoing/progress notifications
        if ((notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) {
            Log.d("MessageNLS", "Ignoring ongoing/progress notification")
            return
        }

        val title = extras.getString(Notification.EXTRA_TITLE) ?: extras.getString("android.title") ?: ""
        val timestamp = sbn.postTime

        // --- Message parsing priority ---
        var senderName: String? = null
        var messageBody: String? = null
        var found = false

        // 1. MessagingStyle (EXTRA_MESSAGES)
        val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
        if (messages != null && messages.isNotEmpty()) {
            // Only consider the latest incoming message
            val last = messages.lastOrNull()
            if (last != null && last is Bundle) {
                val text = last.getCharSequence("text")?.toString() ?: ""
                val sender = last.getCharSequence("sender")?.toString()
                if (text.isNotEmpty()) {
                    messageBody = text
                    senderName = sender
                    found = true
                }
            }
        }

        // 2. EXTRA_BIG_TEXT
        if (!found) {
            val big = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            if (!big.isNullOrEmpty()) {
                messageBody = big.toString()
                found = true
            }
        }

        // 3. EXTRA_TEXT
        if (!found) {
            val t1 = extras.getCharSequence(Notification.EXTRA_TEXT)
            if (!t1.isNullOrEmpty()) {
                messageBody = t1.toString()
                found = true
            }
        }

        if (messageBody.isNullOrEmpty()) {
            Log.d("MessageNLS", "No usable message body found, skipping")
            return
        }

        // Parse sender if not set
        if (senderName == null) {
            val colonIndex = messageBody.indexOf(": ")
            if (colonIndex > 0 && colonIndex < 100) {
                senderName = messageBody.substring(0, colonIndex)
                messageBody = messageBody.substring(colonIndex + 2)
            }
        }

        // --- Filtering rules ---
        val lowerBody = messageBody.lowercase()
        // 1. Ignore outgoing/progress messages
        val sendingPatterns = listOf("sending", "sending video", "sending…", "sending...", "sending…")
        if (sendingPatterns.any { lowerBody.contains(it) }) {
            Log.d("MessageNLS", "Ignoring sending/progress message: $messageBody")
            return
        }

        // 2. Ignore summary notifications
        val summaryPatterns = listOf(
            Regex("\\b\\d+\\s+new messages\\b"),
            Regex("^\\d+\\s+messages$"),
            Regex("^\\d+\\s+new message$"),
            Regex("^new messages?$"),
            Regex("^messages?$"),
            Regex("^\\d+ new messages?$"),
            Regex("^\\d+ new message$"),
            Regex("^new message$"),
            Regex("^\\d+ new$"),
            Regex("^new$"),
            Regex("^1 new message$"),
            Regex("^2 new messages$"),
            Regex("^new messages?$"),
        )
        if (summaryPatterns.any { it.containsMatchIn(lowerBody) } || lowerBody.contains("new messages")) {
            Log.d("MessageNLS", "Ignoring summary notification: $messageBody")
            return
        }

        // 3. Ignore deleted message notifications
        val deletedPatterns = listOf(
            "this message was deleted",
            "you deleted this message",
            "message deleted"
        )
        if (deletedPatterns.any { lowerBody.contains(it) }) {
            Log.d("MessageNLS", "Ignoring deleted-message notification: $messageBody")
            return
        }

        // --- Store in database with de-duplication guard ---
        val message = ChatMessageEntity(
            app = "whatsapp",
            conversationName = title,
            senderName = senderName,
            messageBody = messageBody,
            timestamp = timestamp,
            isPinned = false
        )

        scope.launch {
            try {
                val database = ChatDatabaseManager.getInstance(applicationContext)
                val dao: ChatMessageDao = database.chatMessageDao()

                // Extra safety: avoid inserting near-duplicate messages that WhatsApp may resend
                // (e.g., when deleting or updating a recent message).
                val similarCount = dao.countSimilarMessages(
                    conversationName = title,
                    messageBody = messageBody,
                    timestamp = timestamp
                )
                if (similarCount > 0) {
                    Log.d(
                        "MessageNLS",
                        "Skipping insert; similar message already stored for conversation='$title'"
                    )
                    return@launch
                }

                Log.d("MessageNLS", "Inserting message for conversation='$title', sender='$senderName'")
                // Unique index + IGNORE handles exact duplicates by key
                dao.insert(message)
            } catch (e: Exception) {
                Log.w("MessageNLS", "Insert failed or duplicate", e)
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("MessageNLS", "Notification listener connected")
        // No-op; ensures the service is ready after connect (useful when device unlock state changes)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("MessageNLS", "Notification listener disconnected")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Optional: handle notification removal
    }
}

