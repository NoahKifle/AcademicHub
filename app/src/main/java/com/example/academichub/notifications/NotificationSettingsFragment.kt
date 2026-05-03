package com.example.academichub.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.academichub.databinding.FragmentNotificationSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

/**
 * NotificationSettingsFragment
 * ----------------------------
 * Lets the user:
 *   - Enable/disable each notification type
 *   - Pick what time to receive the daily study reminder
 *   - Test notifications immediately with a "Send Test" button
 */
class NotificationSettingsFragment : Fragment() {

    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = _binding!!

    // Stores the user's chosen study reminder time (default 8:00 PM)
    private var studyHour   = 20
    private var studyMinute = 0

    // Permission request launcher (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showSnackbar("✅ Notifications enabled!")
            applyAllSettings()
        } else {
            showSnackbar("⚠️ Notification permission denied. Enable it in Settings.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatePermissionBanner()
        setupSwitches()
        setupTimePicker()
        setupTestButtons()
    }

    // ── Permission banner ─────────────────────────────────────────────────────

    private fun updatePermissionBanner() {
        val hasPermission = hasNotificationPermission()
        binding.bannerPermission.visibility = if (hasPermission) View.GONE else View.VISIBLE
        binding.btnGrantPermission.setOnClickListener { requestNotificationPermission() }
    }

    // ── Switches ──────────────────────────────────────────────────────────────

    private fun setupSwitches() {
        // Load saved prefs
        val prefs = requireContext().getSharedPreferences("notif_prefs", 0)
        binding.switchDueSoon.isChecked  = prefs.getBoolean("due_soon_enabled",  true)
        binding.switchOverdue.isChecked  = prefs.getBoolean("overdue_enabled",   true)
        binding.switchStudy.isChecked    = prefs.getBoolean("study_enabled",     true)
        studyHour   = prefs.getInt("study_hour",   20)
        studyMinute = prefs.getInt("study_minute",  0)
        updateStudyTimeLabel()

        // Save on change
        binding.switchDueSoon.setOnCheckedChangeListener { _, _ -> applyAllSettings() }
        binding.switchOverdue.setOnCheckedChangeListener { _, _ -> applyAllSettings() }
        binding.switchStudy.setOnCheckedChangeListener   { _, checked ->
            binding.layoutStudyTime.visibility = if (checked) View.VISIBLE else View.GONE
            applyAllSettings()
        }

        // Show/hide time picker based on initial state
        binding.layoutStudyTime.visibility =
            if (binding.switchStudy.isChecked) View.VISIBLE else View.GONE
    }

    // ── Time picker ───────────────────────────────────────────────────────────

    private fun setupTimePicker() {
        binding.btnPickTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(studyHour)
                .setMinute(studyMinute)
                .setTitleText("Set Study Reminder Time")
                .build()

            picker.addOnPositiveButtonClickListener {
                studyHour   = picker.hour
                studyMinute = picker.minute
                updateStudyTimeLabel()
                applyAllSettings()
                showSnackbar("Study reminder set for ${formatTime(studyHour, studyMinute)}")
            }
            picker.show(parentFragmentManager, "time_picker")
        }
    }

    private fun updateStudyTimeLabel() {
        binding.tvStudyTime.text = "Daily at ${formatTime(studyHour, studyMinute)}"
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm  = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0  -> 12
            hour > 12  -> hour - 12
            else       -> hour
        }
        return "$hour12:${minute.toString().padStart(2, '0')} $amPm"
    }

    // ── Test buttons ──────────────────────────────────────────────────────────

    private fun setupTestButtons() {
        binding.btnTestDueSoon.setOnClickListener {
            if (!hasNotificationPermission()) { requestNotificationPermission(); return@setOnClickListener }
            NotificationHelper.showDueSoonNotification(
                context        = requireContext(),
                notificationId = 9001,
                assignmentName = "Chapter 5 Lab: RecyclerView",
                courseName     = "Mobile Programming",
                dueDate        = "tomorrow at 11:59 PM"
            )
            showSnackbar("Test 'Due Soon' notification sent!")
        }

        binding.btnTestOverdue.setOnClickListener {
            if (!hasNotificationPermission()) { requestNotificationPermission(); return@setOnClickListener }
            NotificationHelper.showOverdueNotification(
                context        = requireContext(),
                notificationId = 9002,
                assignmentName = "Midterm Reflection Essay",
                courseName     = "Technical Writing",
                dueDate        = "Apr 8"
            )
            showSnackbar("Test 'Overdue' notification sent!")
        }

        binding.btnTestStudy.setOnClickListener {
            if (!hasNotificationPermission()) { requestNotificationPermission(); return@setOnClickListener }
            NotificationHelper.showStudyReminderNotification(requireContext())
            showSnackbar("Test 'Study Reminder' notification sent!")
        }
    }

    // ── Apply settings ────────────────────────────────────────────────────────

    /**
     * Saves preferences and schedules/cancels alarms based on switch states.
     */
    private fun applyAllSettings() {
        val prefs = requireContext().getSharedPreferences("notif_prefs", 0)
        prefs.edit().apply {
            putBoolean("due_soon_enabled", binding.switchDueSoon.isChecked)
            putBoolean("overdue_enabled",  binding.switchOverdue.isChecked)
            putBoolean("study_enabled",    binding.switchStudy.isChecked)
            putInt("study_hour",   studyHour)
            putInt("study_minute", studyMinute)
            apply()
        }

        // Schedule or cancel daily study reminder
        if (binding.switchStudy.isChecked) {
            NotificationScheduler.scheduleDailyStudyReminder(
                context   = requireContext(),
                hourOfDay = studyHour,
                minute    = studyMinute
            )
        } else {
            NotificationScheduler.cancelDailyStudyReminder(requireContext())
        }

        // Due Soon and Overdue alarms are scheduled per-assignment when
        // assignments are added (in AssignmentsFragment / a future ViewModel).
    }

    // ── Permission helpers ────────────────────────────────────────────────────

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true // Below Android 13, permission is granted at install time
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),
                com.example.academichub.R.color.navy_800))
            .setTextColor(ContextCompat.getColor(requireContext(),
                com.example.academichub.R.color.white))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}