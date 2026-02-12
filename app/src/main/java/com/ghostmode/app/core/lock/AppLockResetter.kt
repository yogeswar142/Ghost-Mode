package com.ghostmode.app.core.lock

import android.app.Activity
import android.content.Intent
import com.ghostmode.app.data.local.chat.database.ChatDatabase
import com.ghostmode.app.data.local.lock.AppLockStorage
import com.ghostmode.app.feature.home.HomeActivity

object AppLockResetter {

    fun resetAppDataAndRestart(activity: Activity) {
        // Clear encrypted preferences
        AppLockStorage.create(activity).clear()

        // Delete Room database
        activity.deleteDatabase(ChatDatabase.DB_NAME)

        // Restart app at Home
        activity.startActivity(
            Intent(activity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        activity.finishAffinity()
    }
}
