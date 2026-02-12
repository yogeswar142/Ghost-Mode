package com.ghostmode.app.feature.whatsapp.pinned

import android.os.Bundle
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
import com.ghostmode.app.databinding.FragmentPinnedBinding
import com.ghostmode.app.feature.whatsapp.chats.ChatMessageAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PinnedFragment : Fragment() {

    private var _binding: FragmentPinnedBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private lateinit var database: ChatDatabase
    private lateinit var adapter: ChatMessageAdapter
    private var pinnedJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("PinnedFragment", "onCreateView called")
        _binding = FragmentPinnedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PinnedFragment", "onViewCreated called")

        try {
            database = ChatDatabaseManager.getInstance(requireContext())
            Log.d("PinnedFragment", "Database initialized successfully")

            setupAdapter()
            setupRecyclerView()
            loadPinnedMessages()
        } catch (e: Exception) {
            Log.e("PinnedFragment", "Error during onViewCreated initialization", e)
            try {
                binding.progressBar.visibility = View.GONE
                updateEmptyState(true)
            } catch (bindingException: IllegalStateException) {
                Log.e(
                    "PinnedFragment",
                    "Binding not available while handling initialization error",
                    bindingException
                )
            }
        }
    }

    private fun setupAdapter() {
        adapter = ChatMessageAdapter(
            onPinClick = { message ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val updatedMessage = message.copy(isPinned = !message.isPinned)
                    try {
                        database.chatMessageDao().update(updatedMessage)
                    } catch (e: Exception) {
                        Log.e("PinnedFragment", "Failed to update pin state", e)
                    }
                }
            },
            onMessageClick = { _ ->
                // Could open a detail view or bottom sheet here
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPinned.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPinned.adapter = adapter
    }

    private fun loadPinnedMessages() {
        Log.d("PinnedFragment", "loadPinnedMessages called")
        pinnedJob?.cancel()
        pinnedJob = viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    database.chatMessageDao().getPinnedMessages().collectLatest { messages ->
                        Log.d("PinnedFragment", "Flow collector received ${messages.size} pinned messages")
                        try {
                            if (binding.progressBar.visibility == View.VISIBLE) {
                                binding.progressBar.visibility = View.GONE
                            }
                            adapter.submitList(messages)
                            updateEmptyState(messages.isEmpty())
                        } catch (e: Exception) {
                            Log.e("PinnedFragment", "UI update failed in Flow collector", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PinnedFragment", "Error while collecting pinned messages Flow", e)
                    try {
                        binding.progressBar.visibility = View.GONE
                        updateEmptyState(true)
                    } catch (bindingException: IllegalStateException) {
                        Log.e(
                            "PinnedFragment",
                            "Binding not available while handling Flow error",
                            bindingException
                        )
                    }
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewPinned.visibility = if (isEmpty) View.GONE else View.VISIBLE
        if (isEmpty) {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        Log.d("PinnedFragment", "onDestroyView called, cancelling pinnedJob and clearing binding")
        pinnedJob?.cancel()
        pinnedJob = null
        _binding = null
        super.onDestroyView()
    }
}

