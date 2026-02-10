package com.ghostmode.app.feature.lock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ghostmode.app.R
import com.ghostmode.app.core.lock.AppLockResetter
import com.ghostmode.app.data.local.lock.AppLockStorage

class LockScreenActivity : AppCompatActivity() {

    private lateinit var appLockStorage: AppLockStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appLockStorage = AppLockStorage.create(this)

        // If for some reason there's no password, skip straight to home.
        if (!appLockStorage.isPasswordSet()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_lock_screen)

        val passwordInput: EditText = findViewById(R.id.editTextPassword)
        val unlockButton: Button = findViewById(R.id.buttonUnlock)
        val resetButton: Button = findViewById(R.id.buttonReset)

        unlockButton.setOnClickListener {
            val passwordText = passwordInput.text?.toString().orEmpty()
            if (passwordText.isEmpty()) {
                Toast.makeText(this, R.string.lock_error_empty_password, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordChars = passwordText.toCharArray()
            val isValid = appLockStorage.verifyPassword(passwordChars)

            if (isValid) {
                navigateToHome()
            } else {
                Toast.makeText(this, R.string.lock_error_invalid_password, Toast.LENGTH_SHORT)
                    .show()
                passwordInput.text?.clear()
            }
        }

        resetButton.setOnClickListener {
            AppLockResetter.resetAppDataAndRestart(this)
        }
    }

    private fun navigateToHome() {
        startActivity(HomeActivityIntentFactory.create(this))
        finish()
    }
}

