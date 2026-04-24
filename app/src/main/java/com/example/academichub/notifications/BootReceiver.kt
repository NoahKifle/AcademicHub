package com.example.academichub.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // Re-schedule the daily study reminder (always active)
        NotificationScheduler.scheduleDailyStudyReminder(
            context   = context,
            hourOfDay = 20, // 8:00 PM
            minute    = 0
        )

    }
}