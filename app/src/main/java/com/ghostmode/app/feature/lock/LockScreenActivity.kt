package com.ghostmode.app.feature.lock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ghostmode.app.R
import com.ghostmode.app.data.local.lock.AppLockStorage
import com.ghostmode.app.feature.home.HomeActivity
import com.ghostmode.app.data.local.chat.ChatDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LockScreenActivity : AppCompatActivity() {

    private lateinit var appLockStorage: AppLockStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appLockStorage = AppLockStorage.create(this)

        if (!appLockStorage.isPasswordSet()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_lock_screen)

        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val unlockButton = findViewById<Button>(R.id.buttonUnlock)
        val resetButton = findViewById<Button>(R.id.buttonReset)

        unlockButton.setOnClickListener {
            val password = passwordInput.text.toString().toCharArray()

            if (password.isEmpty()) {
                Toast.makeText(this, R.string.lock_error_empty_password, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (appLockStorage.verifyPassword(password)) {
                navigateToHome()
            } else {
                Toast.makeText(this, R.string.lock_error_invalid_password, Toast.LENGTH_SHORT).show()
                passwordInput.text.clear()
            }
        }

        resetButton.setOnClickListener {
            // Clear encrypted prefs
            appLockStorage.clear()
            // Also clear any stored chat messages to fully reset app data
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = ChatDatabaseManager.getInstance(this@LockScreenActivity)
                    db.chatMessageDao().deleteAll()
                } catch (e: Exception) {
                    // ignore
                }
            }
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
