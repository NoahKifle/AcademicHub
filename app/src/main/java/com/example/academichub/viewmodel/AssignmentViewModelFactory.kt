package com.example.academichub.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.academichub.model.AssignmentRepository

class AssignmentViewModelFactory(
    private val application: Application,
    private val repository: AssignmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssignmentManagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssignmentManagerViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
