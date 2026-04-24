package com.example.academichub.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar


object NotificationScheduler {

    /**
     * Schedules a "Due Soon" alarm 24 hours before the assignment due time.
     *
     * @param assignmentId  Unique int ID for this assignment (used to cancel later)
     * @param assignmentName  Display name shown in the notification
     * @param courseName  Course the assignment belongs to
     * @param dueTimeMillis  Due date/time as epoch milliseconds
     */
    fun scheduleDueSoonAlarm(
        context: Context,
        assignmentId: Int,
        assignmentName: String,
        courseName: String,
        dueTimeMillis: Long,
        dueDate: String
    ) {
        val triggerAt = dueTimeMillis - (24 * 60 * 60 * 1000L) // 24 hours before
        if (triggerAt <= System.currentTimeMillis()) return    // already passed

        val intent = buildReceiverIntent(
            context = context,
            notificationId = NotificationHelper.NOTIF_ID_DUE_SOON_BASE + assignmentId,
            type = NotificationHelper.TYPE_DUE_SOON,
            assignmentName = assignmentName,
            courseName = courseName,
            dueDate = dueDate
        )
        scheduleAlarm(
            context = context,
            requestCode = NotificationHelper.NOTIF_ID_DUE_SOON_BASE + assignmentId,
            intent = intent,
            triggerAtMillis = triggerAt
        )
    }


    fun scheduleOverdueAlarm(
        context: Context,
        assignmentId: Int,
        assignmentName: String,
        courseName: String,
        dueTimeMillis: Long,
        dueDate: String
    ) {
        if (dueTimeMillis <= System.currentTimeMillis()) return // already overdue

        val intent = buildReceiverIntent(
            context = context,
            notificationId = NotificationHelper.NOTIF_ID_OVERDUE_BASE + assignmentId,
            type = NotificationHelper.TYPE_OVERDUE,
            assignmentName = assignmentName,
            courseName = courseName,
            dueDate = dueDate
        )
        scheduleAlarm(
            context = context,
            requestCode = NotificationHelper.NOTIF_ID_OVERDUE_BASE + assignmentId,
            intent = intent,
            triggerAtMillis = dueTimeMillis
        )
    }


    fun scheduleDailyStudyReminder(
        context: Context,
        hourOfDay: Int = 20,
        minute: Int = 0
    ) {
        // Set trigger to the next occurrence of hourOfDay:minute
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = buildReceiverIntent(
            context = context,
            notificationId = NotificationHelper.NOTIF_ID_STUDY,
            type = NotificationHelper.TYPE_STUDY,
            assignmentName = "",
            courseName = "",
            dueDate = ""
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NotificationHelper.NOTIF_ID_STUDY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Use setRepeating so it fires every day automatically
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    fun cancelDueSoonAlarm(context: Context, assignmentId: Int) {
        cancelAlarm(context, NotificationHelper.NOTIF_ID_DUE_SOON_BASE + assignmentId)
    }


    fun cancelOverdueAlarm(context: Context, assignmentId: Int) {
        cancelAlarm(context, NotificationHelper.NOTIF_ID_OVERDUE_BASE + assignmentId)
    }


    fun cancelDailyStudyReminder(context: Context) {
        cancelAlarm(context, NotificationHelper.NOTIF_ID_STUDY)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun buildReceiverIntent(
        context: Context,
        notificationId: Int,
        type: String,
        assignmentName: String,
        courseName: String,
        dueDate: String
    ): Intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra(NotificationHelper.EXTRA_NOTIFICATION_ID,   notificationId)
        putExtra(NotificationHelper.EXTRA_NOTIFICATION_TYPE, type)
        putExtra(NotificationHelper.EXTRA_ASSIGNMENT_NAME,   assignmentName)
        putExtra(NotificationHelper.EXTRA_COURSE_NAME,       courseName)
        putExtra(NotificationHelper.EXTRA_DUE_DATE,          dueDate)
    }

    private fun scheduleAlarm(
        context: Context,
        requestCode: Int,
        intent: Intent,
        triggerAtMillis: Long
    ) {
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        when {
            // Android 12+ requires exact alarm permission
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                    )
                } else {
                    // Fallback: inexact alarm (still works, just may be delayed slightly)
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                    )
                }
            }
            // Android 6–11
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                )
            }
            // Android 5
            else -> {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                )
            }
        }
    }

    private fun cancelAlarm(context: Context, requestCode: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}