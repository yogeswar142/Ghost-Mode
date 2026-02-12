package com.ghostmode.app.feature.whatsapp.chats

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghostmode.app.data.local.chat.ChatDatabaseManager
import com.ghostmode.app.data.local.chat.database.ChatDatabase
import com.ghostmode.app.data.local.chat.entity.ChatMessageEntity
import com.ghostmode.app.databinding.FragmentChatsBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private lateinit var database: ChatDatabase
    private lateinit var adapter: ChatMessageAdapter
    private var currentSearchQuery: String = ""
    private var messagesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ChatsFragment", "onCreateView called")
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ChatsFragment", "onViewCreated called")

        try {
            database = ChatDatabaseManager.getInstance(requireContext())
            Log.d("ChatsFragment", "Database initialized successfully")

            setupAdapter()
            setupRecyclerView()
            setupSearchBar()
            loadMessages()
        } catch (e: Exception) {
            Log.e("ChatsFragment", "Error during onViewCreated initialization", e)
            // Fail gracefully: show empty state instead of crashing
            try {
                binding.progressBar.visibility = View.GONE
                updateEmptyState(true)
            } catch (bindingException: IllegalStateException) {
                Log.e("ChatsFragment", "Binding not available while handling initialization error", bindingException)
            }
        }
    }

    private fun setupAdapter() {
        adapter = ChatMessageAdapter(
            onPinClick = { message ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val updatedMessage = message.copy(isPinned = !message.isPinned)
                    database.chatMessageDao().update(updatedMessage)
                }
            },
            onMessageClick = { message ->
                // Could open a detail view or bottom sheet here
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChats.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                Log.d("ChatsFragment", "Search query changed to '$currentSearchQuery'")
                loadMessages()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadMessages() {
        Log.d("ChatsFragment", "loadMessages called with query='$currentSearchQuery'")
        messagesJob?.cancel()
        binding.progressBar.visibility = View.VISIBLE
        messagesJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    val flow = if (currentSearchQuery.isEmpty()) {
                        Log.d("ChatsFragment", "Subscribing to all messages Flow")
                        database.chatMessageDao().getAllMessages()
                    } else {
                        val query = "%$currentSearchQuery%"
                        Log.d("ChatsFragment", "Subscribing to search Flow with query='$query'")
                        database.chatMessageDao().searchMessages(query)
                    }

                    flow
                        .distinctUntilChanged()
                        .collectLatest { messages: List<ChatMessageEntity> ->
                            Log.d("ChatsFragment", "Flow collector received ${messages.size} messages")
                            try {
                                Log.d("ChatsFragment", "Submitting list to adapter")
                                adapter.submitList(messages)
                                updateEmptyState(messages.isEmpty())
                            } catch (e: Exception) {
                                Log.e("ChatsFragment", "UI update failed in Flow collector", e)
                            }
                        }
                } catch (e: Exception) {
                    Log.e("ChatsFragment", "Error while collecting messages Flow", e)
                    try {
                        binding.progressBar.visibility = View.GONE
                        updateEmptyState(true)
                    } catch (bindingException: IllegalStateException) {
                        Log.e("ChatsFragment", "Binding not available while handling Flow error", bindingException)
                    }
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewChats.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // Always hide progress once we have handled a state (empty or with data)
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun onDestroyView() {
        Log.d("ChatsFragment", "onDestroyView called, cancelling messagesJob and clearing binding")
        messagesJob?.cancel()
        messagesJob = null
        _binding = null
        super.onDestroyView()
    }
}
