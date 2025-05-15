package com.example.studenttaskmanager.presentation.add_task

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (timestamp: Long) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let(onDateSelected)
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        },
        title = { Text("Выберите дату") },
        text = {
            DatePicker(state = datePickerState)
        }
    )
}