package com.example.academichub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.academichub.databinding.ActivityMainBinding
import com.example.academichub.notifications.NotificationHelper
import com.example.academichub.notifications.NotificationScheduler
import com.example.academichub.notifications.NotificationSettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This is safe to call repeatedly cuz Android ignores duplicates.
        NotificationHelper.createNotificationChannels(this)

        // Schedule the daily study reminder on first launch
        val prefs = getSharedPreferences("notif_prefs", 0)
        val studyEnabled = prefs.getBoolean("study_enabled", true)
        if (studyEnabled) {
            NotificationScheduler.scheduleDailyStudyReminder(
                context   = this,
                hourOfDay = prefs.getInt("study_hour",   20),
                minute    = prefs.getInt("study_minute",  0)
            )
        }

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard      -> DashboardFragment()
                R.id.nav_grades         -> GradesFragment()
                R.id.nav_assignments    -> AssignmentsFragment()
                R.id.nav_notifications  -> NotificationSettingsFragment()
                else                    -> return@setOnItemSelectedListener false
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}