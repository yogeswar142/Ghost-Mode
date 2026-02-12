package com.ghostmode.app.feature.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghostmode.app.R
import com.ghostmode.app.core.permission.NotificationPermissionChecker
import com.ghostmode.app.databinding.ActivityHomeBinding
import com.ghostmode.app.feature.home.adapter.SupportedAppsAdapter
import com.ghostmode.app.feature.home.model.SupportedApp
import com.ghostmode.app.feature.whatsapp.WhatsAppDashboardActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppsList()
        setupSettingsButton()

        if (!NotificationPermissionChecker.isEnabled(this)) {
            NotificationPermissionDialog.show(this)
        }
    }

    private fun setupAppsList() {
        val apps = listOf(
            SupportedApp(
                name = "WhatsApp",
                iconResId = R.drawable.ic_whatsapp,
                targetActivity = WhatsAppDashboardActivity::class
            )
        )

        binding.recyclerViewSupportedApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSupportedApps.adapter = SupportedAppsAdapter(this, apps)
    }

    private fun setupSettingsButton() {
        binding.settingsIcon.setOnClickListener {
            PasswordSecurityDialog.show(this)
        }
    }
}
