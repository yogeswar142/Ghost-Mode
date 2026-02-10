package com.ghostmode.app.feature.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghostmode.app.R
import com.ghostmode.app.core.permission.NotificationPermissionChecker
import com.ghostmode.app.feature.home.adapter.SupportedAppsAdapter
import com.ghostmode.app.feature.home.model.SupportedApp
import com.ghostmode.app.feature.whatsapp.WhatsAppDashboardActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerViewSupportedApps: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        recyclerViewSupportedApps = findViewById(R.id.recyclerViewSupportedApps)

        setupAppsList()
        checkNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission when returning from settings
        // Note: Dialog won't show again if already shown once (first launch only)
        checkNotificationPermission()
    }

    private fun setupAppsList() {
        val supportedApps = createSupportedAppsList()

        val adapter = SupportedAppsAdapter(supportedApps) { intent ->
            startActivity(intent)
        }

        recyclerViewSupportedApps.layoutManager = LinearLayoutManager(this)
        recyclerViewSupportedApps.adapter = adapter
    }

    private fun createSupportedAppsList(): List<SupportedApp> {
        return listOf(
            SupportedApp(
                name = getString(R.string.home_app_whatsapp),
                iconResId = R.drawable.ic_whatsapp,
                targetActivityIntent = Intent(this, WhatsAppDashboardActivity::class.java)
            )
        )
    }

    private fun checkNotificationPermission() {
        val hasShownDialog = sharedPreferences.getBoolean(KEY_PERMISSION_DIALOG_SHOWN, false)
        val isPermissionEnabled = NotificationPermissionChecker.isNotificationListenerEnabled(this)

        // Show dialog on first launch if permission is not enabled
        if (!hasShownDialog && !isPermissionEnabled) {
            NotificationPermissionDialog.show(this)
            sharedPreferences.edit()
                .putBoolean(KEY_PERMISSION_DIALOG_SHOWN, true)
                .apply()
        }
    }

    companion object {
        private const val PREF_NAME = "home_prefs"
        private const val KEY_PERMISSION_DIALOG_SHOWN = "key_permission_dialog_shown"
    }
}

object HomeActivityIntentFactory {
    fun create(context: Context): Intent =
        Intent(context, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
}

