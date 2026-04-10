package com.example.academichub.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = assignment.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Class: ${assignment.classCode}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Due: ${assignment.dueDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${assignment.points} pts",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                onAdd = { name, classCode, dueDate, points ->
                    viewModel.addAssignment(name, classCode, dueDate, points)
                    isDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun AddAssignmentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var classCode by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("") }

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
                        onClick = { onAdd(name, classCode, dueDate, points) },
                        enabled = name.isNotBlank() && classCode.isNotBlank() && dueDate.length == 8 && points.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
