package com.ghostmode.app.core.permission

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

object NotificationPermissionChecker {

    fun isEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false

        val componentName = ComponentName(
            context,
            "com.ghostmode.app.core.notification.MessageNotificationListenerService"
        )

        return enabledListeners.contains(componentName.flattenToString())
    }
}
