package com.ghostmode.app.feature.home.adapter

import android.content.Context
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
    private val context: Context,
    private val apps: List<SupportedApp>
) : RecyclerView.Adapter<SupportedAppsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imageViewAppIcon)
        val name: TextView = view.findViewById(R.id.textViewAppName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supported_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageResource(app.iconResId)
        holder.name.text = app.name

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, app.targetActivity.java))
        }
    }

    override fun getItemCount(): Int = apps.size
}
