package com.ghostmode.app.core.lock

import android.app.Activity
import android.content.Intent
import com.ghostmode.app.data.local.chat.database.ChatDatabase
import com.ghostmode.app.data.local.lock.AppLockStorage
import com.ghostmode.app.feature.home.HomeActivity
import com.ghostmode.app.feature.home.HomeActivityIntentFactory

object AppLockResetter {

    /**
     * Clears all locally stored app-lock data and chat database,
     * then restarts into a fresh [HomeActivity].
     */
    fun resetAppDataAndRestart(activity: Activity) {
        // Clear encrypted app-lock preferences
        AppLockStorage.create(activity).clear()

        // Delete Room database file
        activity.deleteDatabase(ChatDatabase.DB_NAME)

        // Restart into a clean HomeActivity task
        val restartIntent: Intent = HomeActivityIntentFactory.create(activity)
        activity.startActivity(restartIntent)
        activity.finishAffinity()
    }
}

