package com.example.studenttaskmanager.presentation.add_task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.presentation.task_list.toFormattedDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onBack: () -> Unit,
    viewModel: AddTaskViewModel = koinViewModel()
) {

    val state = viewModel.state.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая задача") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
            ,

        ) {
            // Поля ввода
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Название") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = ""
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.subject ?: "",
                onValueChange = viewModel::updateSubject,
                label = { Text("Предмет") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = ""
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = state.professor,
                onValueChange = viewModel::updateProfessor,
                label = { Text("Преподаватель") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = ""
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(padding))

            // Выбор даты
            val showDatePicker = remember { mutableStateOf(false) }
            Button(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = ""
                )

                Text("Дедлайн: ${state.deadline.toFormattedDate()}")
            }
            if (showDatePicker.value) {
//                DatePickerDialog(
//                    onDismissRequest = { showDatePicker.value = false },
//                    onDateSelected = { timestamp ->
//                        viewModel.updateDeadline(timestamp)
//                    }
//                )
                SystemDatePickerDialog(
                    onDateSelected = {
                        viewModel.updateDeadline(it)
                    },
                    onDismissRequest = {
                        showDatePicker.value = false
                    }
                )
            }

            // Приоритет
            Text("Приоритет:", modifier = Modifier.padding(top = 8.dp))
            Row {
                Task.Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = state.priority == priority,
                        onClick = { viewModel.updatePriority(priority) },
                        label = { Text(priority.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Групповая задача
            Switch(
                checked = state.isGroupTask,
                onCheckedChange = { viewModel.toggleGroupTask() },
                modifier = Modifier.padding(top = 8.dp)
            )
            Text("Групповая задача")

            // Кнопка сохранения
            Button(
                onClick = viewModel::addTask,
                enabled = !state.isLoading
                        && state.title.isNotBlank()
                        && state.subject.toString().isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (state.isLoading) CircularProgressIndicator()
                else Text("Сохранить")
            }

            // Ошибки
            state.error?.let { error ->
                Text(
                    text = "Ошибка: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    // Автоматический возврат после успеха
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onBack()
    }
}