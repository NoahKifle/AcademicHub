package com.example.academichub

import android.app.Application
import com.example.academichub.model.AppDatabase
import com.example.academichub.model.AssignmentRepository
import com.example.academichub.model.SettingsRepository
import com.example.academichub.notifications.NotificationHelper

class AcademicHubApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AssignmentRepository(database.assignmentDao()) }
    val settingsRepository by lazy { SettingsRepository(database.userSettingsDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
