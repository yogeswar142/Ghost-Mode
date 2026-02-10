package com.ghostmode.app.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ghostmode.app.data.local.lock.AppLockStorage
import com.ghostmode.app.feature.home.HomeActivity
import com.ghostmode.app.feature.lock.LockScreenActivity

/**
 * Entry activity that decides whether to route to the app lock screen
 * or directly to the home screen.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLockStorage = AppLockStorage.create(this)
        val target = if (appLockStorage.isPasswordSet()) {
            LockScreenActivity::class.java
        } else {
            HomeActivity::class.java
        }

        startActivity(
            Intent(this, target).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
        finish()
    }
}

