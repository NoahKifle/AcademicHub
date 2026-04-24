package com.example.academichub.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId   = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 0)
        val type             = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_TYPE) ?: return
        val assignmentName   = intent.getStringExtra(NotificationHelper.EXTRA_ASSIGNMENT_NAME) ?: ""
        val courseName       = intent.getStringExtra(NotificationHelper.EXTRA_COURSE_NAME) ?: ""
        val dueDate          = intent.getStringExtra(NotificationHelper.EXTRA_DUE_DATE) ?: ""

        when (type) {
            NotificationHelper.TYPE_DUE_SOON -> {
                NotificationHelper.showDueSoonNotification(
                    context        = context,
                    notificationId = notificationId,
                    assignmentName = assignmentName,
                    courseName     = courseName,
                    dueDate        = dueDate
                )
            }
            NotificationHelper.TYPE_OVERDUE -> {
                NotificationHelper.showOverdueNotification(
                    context        = context,
                    notificationId = notificationId,
                    assignmentName = assignmentName,
                    courseName     = courseName,
                    dueDate        = dueDate
                )
            }
            NotificationHelper.TYPE_STUDY -> {
                NotificationHelper.showStudyReminderNotification(context)
            }
        }
    }
}