package com.example.gradecalculator.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gradecalculator.viewmodel.AssignmentManagerViewModel

@Composable
fun AssignmentManagerScreen(
    modifier: Modifier = Modifier,
    viewModel: AssignmentManagerViewModel
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val assignments by viewModel.assignments.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Assignments", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(assignments) { assignment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (assignment.isDone) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ){
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = assignment.isDone,
                            onCheckedChange = { viewModel.toggleDone(assignment.id) }
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {



                            Text(
                                text = assignment.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold

                            )
                            Text(
                                text = "${assignment.assignmentType} for ${assignment.classCode}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Due: ${assignment.dueDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Time: ${formatTime(assignment.timeSpent)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (assignment.isTimerRunning) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${assignment.points} pts",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(onClick = { viewModel.toggleTimer(assignment.id) }) {
                                Icon(
                                    imageVector = if (assignment.isTimerRunning) Icons.Default.Add else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Timer",
                                    tint = if (assignment.isTimerRunning) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }

                            IconButton(onClick = { viewModel.deleteAssignment(assignment.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Assignment",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isDialogOpen = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Assignment")
        }

        if (isDialogOpen) {
            AddAssignmentDialog(
                onDismiss = { isDialogOpen = false },
                onAdd = { name, classCode, dueDate, points, assignmentType ->
                    viewModel.addAssignment(name, classCode, dueDate, points, assignmentType)
                    isDialogOpen = false
                }
            )
        }
    }


private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssignmentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var classCode by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("") }
    val assignmentTypes = listOf("Test", "Quiz", "Homework", "Project", "Paper", "Other")
    var assignmentType by remember { mutableStateOf(assignmentTypes[0])}
    var expanded by remember { mutableStateOf(false) }


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Add Assignment", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Assignment Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {OutlinedTextField(
                    value = assignmentType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assignment Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        assignmentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    assignmentType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = classCode,
                    onValueChange = { classCode = it },
                    label = { Text("Class Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            var formatted = ""
                            for (i in digitsOnly.indices) {
                                formatted += digitsOnly[i]
                                if ((i == 1 || i == 3) && i < 5) {
                                    formatted += "/"
                                }
                            }
                            if (formatted.length <= 8) {
                                dueDate = formatted
                            }
                        },
                        label = { Text("Due Date (mm/dd/yy)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = points,
                        onValueChange = { points = it.filter { it.isDigit() } },
                        label = { Text("Points") },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAdd(name, classCode, dueDate, points, assignmentType) },
                        enabled = name.isNotBlank() && classCode.isNotBlank() && dueDate.length == 8 && points.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
