package com.example.academichub.model

import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val userSettingsDao: UserSettingsDao) {
    val userSettings: Flow<UserSettings?> = userSettingsDao.getUserSettings()

    suspend fun saveSettings(settings: UserSettings) {
        userSettingsDao.saveUserSettings(settings)
    }
}
