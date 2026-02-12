package com.ghostmode.app.feature.home.model

import android.app.Activity
import kotlin.reflect.KClass

data class SupportedApp(
    val name: String,
    val iconResId: Int,
    val targetActivity: KClass<out Activity>
)
