package com.ghostmode.app.feature.whatsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ghostmode.app.R
import com.ghostmode.app.feature.whatsapp.analytics.AnalyticsFragment
import com.ghostmode.app.feature.whatsapp.chats.ChatsFragment
import com.ghostmode.app.feature.whatsapp.pinned.PinnedFragment

class WhatsAppDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsapp_dashboard)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation()

        // Show ChatsFragment as default
        if (savedInstanceState == null) {
            showFragment(ChatsFragment())
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    showFragment(ChatsFragment())
                    true
                }
                R.id.nav_pinned -> {
                    showFragment(PinnedFragment())
                    true
                }
                R.id.nav_analytics -> {
                    showFragment(AnalyticsFragment())
                    true
                }
                else -> false
            }
        }

        // Set Chats as default selected item
        bottomNavigationView.selectedItemId = R.id.nav_chats
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
        }
    }
}

