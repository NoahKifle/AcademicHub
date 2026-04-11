package com.example.gradecalculator.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gradecalculator.model.CalendarDetails
import com.example.gradecalculator.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val days by viewModel.days.collectAsState()

    val monthYearFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.onPreviousMonth() }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
            }
            Text(
                text = monthYearFormatter.format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.onNextMonth() }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(days) { day ->
                CalendarDayItem(
                    day = day,
                    onDateClick = { viewModel.onDateSelected(it.date) }
                )
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    day: CalendarDetails,
    onDateClick: (CalendarDetails) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                if (day.isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
            .clickable { onDateClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.get(Calendar.DAY_OF_MONTH).toString(),
                color = if (day.isSelected) Color.White
                else if (day.isCurrentMonth) MaterialTheme.colorScheme.onSurface
                else Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            if (day.hasAssignments) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.isSelected) Color.White else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}
