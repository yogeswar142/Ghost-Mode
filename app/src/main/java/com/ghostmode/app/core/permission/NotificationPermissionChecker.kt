package com.ghostmode.app.core.permission

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.ghostmode.app.core.notification.MessageNotificationListenerService

object NotificationPermissionChecker {

    /**
     * Checks if Notification Listener Service permission is enabled for this app.
     */
    fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_NOTIFICATION_LISTENERS
        ) ?: return false

        val componentName = ComponentName(
            context,
            MessageNotificationListenerService::class.java
        )

        return enabledListeners.contains(componentName.flattenToString())
    }
}
