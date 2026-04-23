package com.example.academichub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academichub.model.AssignmentDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AssignmentManagerViewModel : ViewModel() {
    private val _assignments = MutableStateFlow<List<AssignmentDetails>>(emptyList())
    val assignments: StateFlow<List<AssignmentDetails>> = _assignments.asStateFlow()
    private var timerJob: Job? = null

    fun addAssignment(name: String, classCode: String, dueDate: String, points: String, assignmentType: String) {
        if (name.isBlank() || classCode.isBlank() || dueDate.isBlank() || points.isBlank() ||
            assignmentType.isBlank()) return

        val newAssignment = AssignmentDetails(name = name, classCode = classCode, dueDate = dueDate,
            points = points, assignmentType = assignmentType)
        _assignments.update {
            it + newAssignment
        }
    }

    fun deleteAssignment(assignmentId: String) {
        _assignments.update { currentList ->
            currentList.filterNot { it.id == assignmentId }
        }
        checkTimerJob()
    }

    fun toggleTimer(assignmentId: String) {
        _assignments.update { currentList ->
            currentList.map {
                if (it.id == assignmentId) {
                    it.copy(isTimerRunning = !it.isTimerRunning)
                } else {
                    it
                }
            }
        }
        checkTimerJob()
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
                _assignments.update { currentList ->
                    currentList.map {
                        if (it.isTimerRunning) {
                            it.copy(timeSpent = it.timeSpent + 1)
                        } else {
                            it
                        }
                    }
                }
            }
        }
    }
    
    fun toggleDone(assignmentId: String, earnedPoints: String = "") {
        _assignments.update { currentList ->
            currentList.map {
                if (it.id == assignmentId) {
                    it.copy(isDone = !it.isDone, earnedPoints = earnedPoints)
                } else {
                    it
                }
            }
        }
    }

    fun updateEarnedPoints(assignmentId: String, earnedPoints: String) {
        _assignments.update { currentList ->
            currentList.map {
                if (it.id == assignmentId) {
                    it.copy(earnedPoints = earnedPoints)
                } else {
                    it
                }
            }
        }
    }
}
