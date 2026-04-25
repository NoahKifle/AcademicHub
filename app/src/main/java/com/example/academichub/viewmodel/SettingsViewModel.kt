package com.example.academichub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academichub.model.SettingsRepository
import com.example.academichub.model.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val userSettings: StateFlow<UserSettings?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveSettings(
        name: String,
        startDate: String,
        endDate: String,
        targetGpa: Double,
        currentGpa: Double
    ) {
        viewModelScope.launch {
            repository.saveSettings(
                UserSettings(
                    userName = name,
                    semesterStartDate = startDate,
                    semesterEndDate = endDate,
                    targetGpa = targetGpa,
                    currentGpa = currentGpa
                )
            )
        }
    }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
