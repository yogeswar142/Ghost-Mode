package com.ghostmode.app.feature.home

import android.app.Dialog
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ghostmode.app.R

object NotificationPermissionDialog {

    /**
     * Shows a non-blocking dialog explaining notification access requirement.
     * Provides "Enable" and "Later" buttons.
     */
    fun show(activity: AppCompatActivity): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_message)
            .setPositiveButton(R.string.permission_dialog_enable) { _, _ ->
                openNotificationListenerSettings(activity)
            }
            .setNegativeButton(R.string.permission_dialog_later) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .apply {
                show()
            }
    }

    private fun openNotificationListenerSettings(activity: AppCompatActivity) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        activity.startActivity(intent)
    }
}
