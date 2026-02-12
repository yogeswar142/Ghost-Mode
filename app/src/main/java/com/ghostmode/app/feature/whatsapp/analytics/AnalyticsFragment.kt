package com.ghostmode.app.feature.whatsapp.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ghostmode.app.databinding.FragmentAnalyticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    bindStats(state)
                    bindMessagesPerDay(state)
                    bindMessagesPerConversation(state)
                }
            }
        }
    }

    private fun setupCharts() {
        // Basic styling for line chart
        binding.messagesPerDayChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            legend.isEnabled = false
        }

        // Basic styling for bar chart
        binding.messagesPerConversationChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            legend.isEnabled = false
        }
    }

    private fun bindStats(state: AnalyticsUiState) {
        binding.totalMessagesValue.text = state.totalMessages.toString()
        binding.totalPinnedValue.text = state.totalPinned.toString()
        binding.messagesTodayValue.text = state.messagesToday.toString()
    }

    private fun bindMessagesPerDay(state: AnalyticsUiState) {
        if (state.messagesPerDay.isEmpty()) {
            binding.messagesPerDayChart.clear()
            return
        }

        // Sort by day ascending
        val entries = state.messagesPerDay.entries
            .sortedBy { it.key }
            .mapIndexed { index, (dayMillis, count) ->
                Entry(index.toFloat(), count.toFloat()).apply {
                    data = dayMillis
                }
            }

        val dataSet = LineDataSet(entries, "").apply {
            color = Color.parseColor("#1E88E5")
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 3f
            setDrawValues(false)
        }

        binding.messagesPerDayChart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
            state.messagesPerDay.entries
                .sortedBy { it.key }
                .map { formatDayLabel(it.key) }
        )

        binding.messagesPerDayChart.data = LineData(dataSet)
        binding.messagesPerDayChart.invalidate()
    }

    private fun bindMessagesPerConversation(state: AnalyticsUiState) {
        if (state.messagesPerConversation.isEmpty()) {
            binding.messagesPerConversationChart.clear()
            return
        }

        val conversations = state.messagesPerConversation.entries
            .sortedByDescending { it.value }
            .map { it.key }

        val entries = state.messagesPerConversation.entries
            .sortedByDescending { it.value }
            .mapIndexed { index, (name, count) ->
                BarEntry(index.toFloat(), count.toFloat()).apply {
                    data = name
                }
            }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#43A047")
            setDrawValues(false)
        }

        binding.messagesPerConversationChart.xAxis.valueFormatter =
            com.github.mikephil.charting.formatter.IndexAxisValueFormatter(conversations)

        val data = BarData(dataSet)
        data.barWidth = 0.6f
        binding.messagesPerConversationChart.data = data
        binding.messagesPerConversationChart.invalidate()
    }

    private fun formatDayLabel(dayMillis: Long): String {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        return sdf.format(Date(dayMillis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

