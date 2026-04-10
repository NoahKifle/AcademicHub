package com.example.gradecalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.academichub.model.AssignmentDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AssignmentManagerViewModel : ViewModel() {
    private val _assignments = MutableStateFlow<List<AssignmentDetails>>(emptyList())
    val assignments: StateFlow<List<AssignmentDetails>> = _assignments.asStateFlow()

    fun addAssignment(name: String, classCode: String, dueDate: String, points: String) {
        if (name.isBlank() || classCode.isBlank() || dueDate.isBlank() || points.isBlank()) return

        val newAssignment = AssignmentDetails(name, classCode, dueDate, points)
        _assignments.update { it + newAssignment }
    }
}
