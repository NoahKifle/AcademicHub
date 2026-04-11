package com.example.gradecalculator.model

import java.util.Calendar
import java.util.UUID

data class CalendarDetails(
    val id: String = UUID.randomUUID().toString(),
    val date: Calendar,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false,
    val hasAssignments: Boolean = false
)
