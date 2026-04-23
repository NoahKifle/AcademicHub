package com.example.academichub.model

import java.util.UUID

data class AssignmentDetails(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val classCode: String,
    val dueDate: String,
    val points: String,
    val earnedPoints: String = "",
    val assignmentType: String = "Homework",
    val isDone: Boolean = false,
    val timeSpent: Long = 0L,
    val isTimerRunning: Boolean = false
)
