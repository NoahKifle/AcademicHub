package com.example.academichub.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.academichub.model.AssignmentDetails
import com.example.academichub.model.AssignmentRepository
import com.example.academichub.notifications.NotificationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AssignmentManagerViewModel(
    application: Application,
    private val repository: AssignmentRepository
) : AndroidViewModel(application) {

    private val _assignments = MutableStateFlow<List<AssignmentDetails>>(emptyList())
    val assignments: StateFlow<List<AssignmentDetails>> = _assignments.asStateFlow()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.allAssignments.collect { list ->
                if (timerJob == null || timerJob?.isActive == false) {
                    _assignments.value = list
                }
            }
        }
    }

    fun addAssignment(
        name: String,
        classCode: String,
        dueDate: String,
        points: String,
        assignmentType: String
    ) {
        if (name.isBlank() || classCode.isBlank() || dueDate.isBlank() || points.isBlank() ||
            assignmentType.isBlank()
        ) return

        val newAssignment = AssignmentDetails(
            name = name,
            classCode = classCode,
            dueDate = dueDate,
            points = points,
            assignmentType = assignmentType
        )
        viewModelScope.launch {
            repository.insert(newAssignment)
            scheduleNotifications(newAssignment)
        }
    }

    private fun scheduleNotifications(assignment: AssignmentDetails) {
        try {
            val sdf = SimpleDateFormat("MM/dd/yy", Locale.US)
            val date = sdf.parse(assignment.dueDate) ?: return
            val timeMillis = date.time

            val alarmId = assignment.id.hashCode()

            NotificationScheduler.scheduleDueSoonAlarm(
                getApplication(),
                alarmId,
                assignment.name,
                assignment.classCode,
                timeMillis,
                assignment.dueDate
            )

            NotificationScheduler.scheduleOverdueAlarm(
                getApplication(),
                alarmId,
                assignment.name,
                assignment.classCode,
                timeMillis,
                assignment.dueDate
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            repository.deleteById(assignmentId)

            val alarmId = assignmentId.hashCode()
            NotificationScheduler.cancelDueSoonAlarm(getApplication(), alarmId)
            NotificationScheduler.cancelOverdueAlarm(getApplication(), alarmId)

            checkTimerJob()
        }
    }

    fun toggleTimer(assignmentId: String) {
        val assignment = _assignments.value.find { it.id == assignmentId }
        assignment?.let {
            val isStarting = !it.isTimerRunning
            val updated = it.copy(isTimerRunning = isStarting)

            _assignments.update { list ->
                list.map { item -> if (item.id == assignmentId) updated else item }
            }

            if (!isStarting) {
                viewModelScope.launch {
                    repository.update(updated)
                }
            }
            checkTimerJob()
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
        val assignment = _assignments.value.find { it.id == assignmentId }
        assignment?.let {
            val updated =
                it.copy(isDone = !it.isDone, earnedPoints = earnedPoints, isTimerRunning = false)
            viewModelScope.launch {
                repository.update(updated)

                if (updated.isDone) {
                    val alarmId = assignmentId.hashCode()
                    NotificationScheduler.cancelDueSoonAlarm(getApplication(), alarmId)
                    NotificationScheduler.cancelOverdueAlarm(getApplication(), alarmId)
                }

                checkTimerJob()
            }
        }
    }
}
