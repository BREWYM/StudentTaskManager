package com.example.studenttaskmanager.presentation.task_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studenttaskmanager.common.DropdownSelector
import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.Task
import java.time.LocalDate

@Composable
fun FilterSection(
    state: TaskListState,
    onStatusChanged: (Task.Priority?) -> Unit,
    onProfessorChanged: (String?) -> Unit,
    onDateChanged: (LocalDate?) -> Unit,
    onSubjectChanged: (Subject?) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Фильтры", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = onClearFilters) {
                Text("Очистить")
            }
        }

        DropdownSelector(
            label = "Приоритет",
            options = Task.Priority.entries,
            selected = state.statusFilter,
            onSelected = onStatusChanged
        )

        OutlinedTextField(
            value = state.professorFilter ?: "",
            onValueChange = { onProfessorChanged(it.takeIf { it.isNotBlank() }) },
            label = { Text("Преподаватель") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        DatePickerField(
            label = "Дата дедлайна",
            selectedDate = state.dateFilter,
            onDateSelected = onDateChanged
        )

        val subjectOptions = state.actualTasks.mapNotNull { it.subject }.distinct()

        DropdownSelector(
            label = "Предмет",
            options = subjectOptions,
            selected = state.subjectFilter,
            onSelected = onSubjectChanged
        )

    }
}