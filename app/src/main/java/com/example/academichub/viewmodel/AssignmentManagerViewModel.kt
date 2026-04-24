package com.example.academichub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academichub.model.AssignmentDetails
import com.example.academichub.model.AssignmentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AssignmentManagerViewModel(private val repository: AssignmentRepository) : ViewModel() {
    private val _assignments = MutableStateFlow<List<AssignmentDetails>>(emptyList())
    val assignments: StateFlow<List<AssignmentDetails>> = _assignments.asStateFlow()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.allAssignments.collect { list ->
                _assignments.value = list
                checkTimerJob()
            }
        }
    }

    fun addAssignment(name: String, classCode: String, dueDate: String, points: String, assignmentType: String) {
        if (name.isBlank() || classCode.isBlank() || dueDate.isBlank() || points.isBlank() ||
            assignmentType.isBlank()) return

        val newAssignment = AssignmentDetails(
            name = name,
            classCode = classCode,
            dueDate = dueDate,
            points = points,
            assignmentType = assignmentType
        )
        viewModelScope.launch {
            repository.insert(newAssignment)
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            repository.deleteById(assignmentId)
        }
    }

    fun toggleTimer(assignmentId: String) {
        val assignment = _assignments.value.find { it.id == assignmentId }
        assignment?.let {
            val updated = it.copy(isTimerRunning = !it.isTimerRunning)
            viewModelScope.launch {
                repository.update(updated)
            }
        }
    }

    private fun checkTimerJob() {
        val anyRunning = _assignments.value.any { it.isTimerRunning }
        if (anyRunning && (timerJob == null || timerJob?.isActive == false)) {
            startTimerJob()
        } else if (!anyRunning) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    private fun startTimerJob() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _assignments.value.filter { it.isTimerRunning }.forEach {
                    repository.update(it.copy(timeSpent = it.timeSpent + 1))
                }
            }
        }
    }
    
    fun toggleDone(assignmentId: String, earnedPoints: String = "") {
        val assignment = _assignments.value.find { it.id == assignmentId }
        assignment?.let {
            val updated = it.copy(isDone = !it.isDone, earnedPoints = earnedPoints)
            viewModelScope.launch {
                repository.update(updated)
            }
        }
    }

    fun updateEarnedPoints(assignmentId: String, earnedPoints: String) {
        val assignment = _assignments.value.find { it.id == assignmentId }
        assignment?.let {
            val updated = it.copy(earnedPoints = earnedPoints)
            viewModelScope.launch {
                repository.update(updated)
            }
        }
    }
}
