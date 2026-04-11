package com.example.gradecalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gradecalculator.model.AssignmentDetails
import com.example.gradecalculator.model.CalendarDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class CalendarViewModel : ViewModel() {
    private val _currentMonth = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    })
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    private val _days = MutableStateFlow<List<CalendarDetails>>(emptyList())
    val days: StateFlow<List<CalendarDetails>> = _days.asStateFlow()

    private var currentAssignments: List<AssignmentDetails> = emptyList()

    init {
        generateCalendarDays(_currentMonth.value)
    }

    fun setAssignments(assignments: List<AssignmentDetails>) {
        currentAssignments = assignments
        generateCalendarDays(_currentMonth.value)
    }

    fun onDateSelected(date: Calendar) {
        _selectedDate.value = date.clone() as Calendar
        generateCalendarDays(_currentMonth.value)
    }

    fun onPreviousMonth() {
        _currentMonth.update {
            (it.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
        }
        generateCalendarDays(_currentMonth.value)
    }

    fun onNextMonth() {
        _currentMonth.update {
            (it.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        }
        generateCalendarDays(_currentMonth.value)
    }

    private fun generateCalendarDays(month: Calendar) {
        val daysList = mutableListOf<CalendarDetails>()

        val firstDayOfMonth = month.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

        val prevMonth = firstDayOfMonth.clone() as Calendar
        prevMonth.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in firstDayOfWeek - 1 downTo 0) {
            val date = prevMonth.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, daysInPrevMonth - i)
            daysList.add(
                CalendarDetails(
                    date = date,
                    isCurrentMonth = false,
                    hasAssignments = checkHasAssignments(date)
                )
            )
        }

        val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            val date = month.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day)
            daysList.add(
                CalendarDetails(
                    date = date,
                    isCurrentMonth = true,
                    isSelected = isSameDay(date, _selectedDate.value),
                    hasAssignments = checkHasAssignments(date)
                )
            )
        }

        val remainingDays = 42 - daysList.size
        val nextMonth = month.clone() as Calendar
        nextMonth.add(Calendar.MONTH, 1)
        for (day in 1..remainingDays) {
            val date = nextMonth.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day)
            daysList.add(
                CalendarDetails(
                    date = date,
                    isCurrentMonth = false,
                    hasAssignments = checkHasAssignments(date)
                )
            )
        }

        _days.value = daysList
    }

    private fun checkHasAssignments(date: Calendar): Boolean {
        val dateString = String.format("%02d/%02d/%02d",
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.DAY_OF_MONTH),
            date.get(Calendar.YEAR) % 100
        )
        return currentAssignments.any { it.dueDate == dateString }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
