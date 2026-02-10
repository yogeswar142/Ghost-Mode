package com.ghostmode.app.feature.home.model

import android.content.Intent

/**
 * Represents a supported messaging app in the home screen list.
 */
data class SupportedApp(
    val name: String,
    val iconResId: Int,
    val targetActivityIntent: Intent
)
