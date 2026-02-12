package com.ghostmode.app.feature.home

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.ghostmode.app.R

object NotificationPermissionDialog {

    fun show(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_message)
            .setPositiveButton(R.string.permission_dialog_enable) { _, _ ->
                activity.startActivity(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                )
            }
            .setNegativeButton(R.string.permission_dialog_later, null)
            .show()
    }
}
