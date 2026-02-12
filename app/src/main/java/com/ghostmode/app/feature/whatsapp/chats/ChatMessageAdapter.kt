package com.ghostmode.app.feature.whatsapp.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ghostmode.app.R
import com.ghostmode.app.data.local.chat.entity.ChatMessageEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.view.HapticFeedbackConstants
import java.util.concurrent.TimeUnit

class ChatMessageAdapter(
    private val onPinClick: (ChatMessageEntity) -> Unit,
    private val onMessageClick: (ChatMessageEntity) -> Unit
) : ListAdapter<ChatMessageEntity, ChatMessageAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val conversationNameText: TextView = itemView.findViewById(R.id.conversationNameText)
        private val senderNameText: TextView = itemView.findViewById(R.id.senderNameText)
        private val messagePreviewText: TextView = itemView.findViewById(R.id.messagePreviewText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        private val pinIcon: ImageView = itemView.findViewById(R.id.pinIcon)

        fun bind(message: ChatMessageEntity) {
            conversationNameText.text = message.conversationName

            if (!message.senderName.isNullOrEmpty()) {
                senderNameText.text = message.senderName
                senderNameText.visibility = android.view.View.VISIBLE
            } else {
                senderNameText.visibility = android.view.View.GONE
            }

            messagePreviewText.text = message.messageBody

            // Format timestamp into friendly relative text
            timestampText.text = formatRelativeTime(message.timestamp)

            // Update pin icon based on isPinned
            pinIcon.setImageResource(
                if (message.isPinned) R.drawable.ic_pin_filled else R.drawable.ic_pin
            )

            pinIcon.setOnClickListener {
                // light haptic feedback
                pinIcon.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onPinClick(message)
            }

            itemView.setOnClickListener {
                onMessageClick(message)
            }
        }
    }

    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days == 1L -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatMessageEntity>() {
            override fun areItemsTheSame(
                oldItem: ChatMessageEntity,
                newItem: ChatMessageEntity
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ChatMessageEntity,
                newItem: ChatMessageEntity
            ): Boolean = oldItem == newItem
        }
    }
}
