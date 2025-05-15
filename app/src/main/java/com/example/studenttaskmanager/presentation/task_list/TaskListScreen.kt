package com.example.studenttaskmanager.presentation.task_list

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studenttaskmanager.domain.models.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = koinViewModel(),
    navController: NavController,
) {
    val state = viewModel.state.value

    // Логируем изменения списка (можно убрать потом)
    LaunchedEffect(state.tasks) {
        Log.d("TaskListScreen", "Tasks size = ${state.tasks.size}")
    }

// 1) берём текущее backStackEntry как State
    val backStackEntry by navController.currentBackStackEntryAsState()
    // 2) достаём из него currentRoute
    val currentRoute = backStackEntry?.destination?.route

    // 3) как только маршрут становится "tasks", загружаем задачи
    LaunchedEffect(currentRoute) {
        if (currentRoute == "tasks") {
            viewModel.loadTasks(Firebase.auth.currentUser?.uid ?: "")
        }
    }

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ошибка: ${state.error}")
        }

        else -> Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Задачи") },
                    actions = {
                        IconButton(onClick = { navController.navigate("add_task") }) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить")
                        }
                    }
                )
            }
        ) { inner ->
            val tasks = state.tasks
            if (tasks.isEmpty()) {
                // Если список реально пуст — покажем заглушку
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text("Задач пока нет")

                    }
                }
            } else {
                TaskListContent(
                    tasks = tasks,
                    modifier = Modifier.padding(inner)
                )
            }
        }
    }
}

@Composable
private fun TaskListContent(tasks: List<Task>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = tasks, key = { it.id }) { task ->
            TaskItem(task = task)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(task.title, style = MaterialTheme.typography.titleMedium)
            Text("Дедлайн: ${task.deadline.toFormattedDate()}")
        }
    }
}

fun Long.toFormattedDate(): String =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(this))