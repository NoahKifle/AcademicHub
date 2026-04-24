package com.example.academichub.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.academichub.MainActivity
import com.example.academichub.R


object NotificationHelper {

    // ── Channel IDs ───────────────────────────────────────────────────────────
    const val CHANNEL_DUE_SOON   = "channel_due_soon"
    const val CHANNEL_OVERDUE    = "channel_overdue"
    const val CHANNEL_STUDY      = "channel_study_reminder"

    // ── Notification IDs (unique per notification type) ───────────────────────
    const val NOTIF_ID_DUE_SOON_BASE = 1000  // +assignmentId for uniqueness
    const val NOTIF_ID_OVERDUE_BASE  = 2000
    const val NOTIF_ID_STUDY         = 3000

    // ── Extra keys passed via Intent to NotificationReceiver ─────────────────
    const val EXTRA_NOTIFICATION_ID    = "extra_notification_id"
    const val EXTRA_NOTIFICATION_TYPE  = "extra_notification_type"
    const val EXTRA_ASSIGNMENT_NAME    = "extra_assignment_name"
    const val EXTRA_COURSE_NAME        = "extra_course_name"
    const val EXTRA_DUE_DATE           = "extra_due_date"

    // ── Notification types ────────────────────────────────────────────────────
    const val TYPE_DUE_SOON  = "due_soon"
    const val TYPE_OVERDUE   = "overdue"
    const val TYPE_STUDY     = "study"


    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            // Channel 1: Due Soon (high priority — pops on screen)
            NotificationChannel(
                CHANNEL_DUE_SOON,
                "Due Soon Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when an assignment is due within 24 hours"
                enableVibration(true)
                manager.createNotificationChannel(this)
            }

            // Channel 2: Overdue (high priority)
            NotificationChannel(
                CHANNEL_OVERDUE,
                "Overdue Assignments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when an assignment deadline has passed"
                enableVibration(true)
                manager.createNotificationChannel(this)
            }

            // Channel 3: Study Reminder (default priority — silent nudge)
            NotificationChannel(
                CHANNEL_STUDY,
                "Daily Study Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder to log study hours"
                manager.createNotificationChannel(this)
            }
        }
    }


    fun showDueSoonNotification(
        context: Context,
        notificationId: Int,
        assignmentName: String,
        courseName: String,
        dueDate: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DUE_SOON)
            .setSmallIcon(R.drawable.ic_calendar_small)
            .setContentTitle("⏰ Due Soon: $assignmentName")
            .setContentText("$courseName • Due $dueDate")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Don't forget! \"$assignmentName\" for $courseName is due on $dueDate. Tap to open AcademicHub."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF2EC4B6.toInt()) // teal accent
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }


    fun showOverdueNotification(
        context: Context,
        notificationId: Int,
        assignmentName: String,
        courseName: String,
        dueDate: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_OVERDUE)
            .setSmallIcon(R.drawable.ic_assignment_small)
            .setContentTitle("🚨 Overdue: $assignmentName")
            .setContentText("$courseName • Was due $dueDate")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("\"$assignmentName\" for $courseName was due on $dueDate and hasn't been marked complete. Tap to open AcademicHub."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFFE63946.toInt()) // red accent
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }


    fun showStudyReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIF_ID_STUDY, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STUDY)
            .setSmallIcon(R.drawable.ic_timer_small)
            .setContentTitle("📚 Time to Study!")
            .setContentText("Don't forget to log your study hours today.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Keep your streak going! Log your study hours for today in AcademicHub and stay on track with your goals."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF2EC4B6.toInt())
            .build()

        NotificationManagerCompat.from(context).notify(NOTIF_ID_STUDY, notification)
    }
}