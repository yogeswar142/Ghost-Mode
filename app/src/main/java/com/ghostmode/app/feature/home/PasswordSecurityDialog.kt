package com.ghostmode.app.feature.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.ghostmode.app.R
import com.ghostmode.app.data.local.lock.AppLockStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PasswordSecurityDialog {

    fun show(context: Context) {
        val storage = AppLockStorage.create(context)

        if (storage.isPasswordSet()) {
            showUpdatePasswordDialog(context, storage)
        } else {
            showSetPasswordDialog(context, storage)
        }
    }

    private fun showSetPasswordDialog(context: Context, storage: AppLockStorage) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_set_password, null)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val confirmInput = view.findViewById<EditText>(R.id.confirmPasswordInput)

        AlertDialog.Builder(context)
            .setTitle(R.string.set_password)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val password = passwordInput.text.toString()
                val confirm = confirmInput.text.toString()

                when {
                    password.isEmpty() -> {
                        Toast.makeText(context, R.string.password_empty_error, Toast.LENGTH_SHORT)
                            .show()
                    }

                    password != confirm -> {
                        Toast.makeText(context, R.string.passwords_do_not_match, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        storage.setPassword(password.toCharArray())
                        Toast.makeText(
                            context,
                            R.string.password_set_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showUpdatePasswordDialog(
        context: Context,
        storage: AppLockStorage
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_update_password, null)
        val oldPasswordInput = view.findViewById<EditText>(R.id.oldPasswordInput)
        val newPasswordInput = view.findViewById<EditText>(R.id.newPasswordInput)
        val confirmInput = view.findViewById<EditText>(R.id.confirmPasswordInput)

        AlertDialog.Builder(context)
            .setTitle(R.string.update_password)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val oldPassword = oldPasswordInput.text.toString().toCharArray()
                val newPassword = newPasswordInput.text.toString()
                val confirm = confirmInput.text.toString()

                when {
                    !storage.verifyPassword(oldPassword) -> {
                        Toast.makeText(context, R.string.old_password_incorrect, Toast.LENGTH_SHORT)
                            .show()
                    }

                    newPassword.isEmpty() -> {
                        Toast.makeText(context, R.string.password_empty_error, Toast.LENGTH_SHORT)
                            .show()
                    }

                    newPassword != confirm -> {
                        Toast.makeText(context, R.string.passwords_do_not_match, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        storage.setPassword(newPassword.toCharArray())
                        Toast.makeText(
                            context,
                            R.string.password_updated_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
