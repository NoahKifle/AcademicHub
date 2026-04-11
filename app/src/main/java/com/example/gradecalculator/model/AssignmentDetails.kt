package com.example.gradecalculator.model

import java.util.UUID

data class AssignmentDetails(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val classCode: String,
    val dueDate: String,
    val points: String,
    val timeSpent: Long = 0L,
    val isTimerRunning: Boolean = false,
    val assignmentType: String,
    val isDone: Boolean = false
)
