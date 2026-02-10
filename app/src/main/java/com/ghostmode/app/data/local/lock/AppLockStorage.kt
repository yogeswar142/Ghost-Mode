package com.ghostmode.app.data.local.lock

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ghostmode.app.core.security.PasswordHasher
import java.util.Arrays

class AppLockStorage(
    private val encryptedSharedPreferences: SharedPreferences
) {

    fun isPasswordSet(): Boolean {
        return encryptedSharedPreferences.contains(KEY_PASSWORD_HASH) &&
            encryptedSharedPreferences.contains(KEY_SALT)
    }

    /**
     * Stores only a derived key (hash) and random salt.
     * The provided [password] char array is wiped after use.
     */
    fun setPassword(password: CharArray) {
        if (password.isEmpty()) return

        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash(password, salt)

        // Immediately wipe password from memory
        Arrays.fill(password, '\u0000')

        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

        encryptedSharedPreferences.edit()
            .putString(KEY_SALT, saltBase64)
            .putString(KEY_PASSWORD_HASH, hashBase64)
            .apply()
    }

    /**
     * Verifies the given [password] against the stored hash.
     * The provided [password] char array is wiped after use.
     */
    fun verifyPassword(password: CharArray): Boolean {
        if (!isPasswordSet()) {
            Arrays.fill(password, '\u0000')
            return false
        }

        val saltBase64 = encryptedSharedPreferences.getString(KEY_SALT, null)
        val hashBase64 = encryptedSharedPreferences.getString(KEY_PASSWORD_HASH, null)

        if (saltBase64.isNullOrEmpty() || hashBase64.isNullOrEmpty()) {
            Arrays.fill(password, '\u0000')
            return false
        }

        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val expectedHash = Base64.decode(hashBase64, Base64.NO_WRAP)

        val result = PasswordHasher.verify(password, salt, expectedHash)

        // Wipe password after verification
        Arrays.fill(password, '\u0000')

        return result
    }

    fun clear() {
        encryptedSharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "app_lock_prefs"
        private const val KEY_PASSWORD_HASH = "key_password_hash"
        private const val KEY_SALT = "key_salt"

        fun create(context: Context): AppLockStorage {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return AppLockStorage(prefs)
        }
    }
}
