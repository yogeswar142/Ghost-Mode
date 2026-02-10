package com.ghostmode.app.feature.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ghostmode.app.R
import com.ghostmode.app.feature.home.model.SupportedApp

class SupportedAppsAdapter(
    private val apps: List<SupportedApp>,
    private val onAppClick: (Intent) -> Unit
) : RecyclerView.Adapter<SupportedAppsAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supported_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount(): Int = apps.size

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.imageViewAppIcon)
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewAppName)

        fun bind(app: SupportedApp) {
            iconImageView.setImageResource(app.iconResId)
            nameTextView.text = app.name

            itemView.setOnClickListener {
                onAppClick(app.targetActivityIntent)
            }
        }
    }
}
