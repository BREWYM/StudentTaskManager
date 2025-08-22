package com.example.studenttaskmanager.presentation.task_details

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studenttaskmanager.presentation.task_list.toFormattedDate
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailsScreen(
    taskId: String,
    subjectNameFromArgs: String,
    navController: NavController,
    viewModel: TaskDetailsViewModel = koinViewModel(),
) {
    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    val task = viewModel.task
    val context = LocalContext.current

    var editableSubjectName by rememberSaveable { mutableStateOf(subjectNameFromArgs) }


    task?.let {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = it.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Название") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,  // замените на нужный
                        contentDescription = "Заголовок"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = editableSubjectName,
                onValueChange = {
                    editableSubjectName = it
                    viewModel.updateSubject(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Предмет"
                    )
                },
                label = { Text("Предмет") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = it.professor,
                onValueChange = viewModel::updateProfessor,
                label = { Text("Преподаватель") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Предмет"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
//            OutlinedTextField(
//                value = it.deadline.toString(),
//                onValueChange = viewModel::updateDate,
//                label = { Text("Дедлайн") },
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
            OutlinedTextField(
                value = it.deadline.toFormattedDate(), // строковое представление даты
                onValueChange = viewModel::updateDate,
                label = { Text("Дедлайн") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Дедлайн"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = it.priority.toString(),
                onValueChange = viewModel::updatePriority,
                label = { Text("Приоритет") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Дата (можно DatePicker потом засунуть)
            Text(
                "Дата: ${
                    it.deadline.let { millis ->
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
                    } ?: "не установлена"
                }")

            Spacer(Modifier.height(8.dp))

            // Комментарии
            Text("Комментарии:")
            it.comments.forEach { comment ->
                Text("- $comment", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.newComment,
                onValueChange = { viewModel.newComment = it },
                label = { Text("Новый комментарий") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { viewModel.addComment() }) {
                Text("Добавить комментарий")
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    viewModel.save()
                    Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) {
                    Text("Сохранить")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = {
                        viewModel.delete {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Удалить", color = Color.White)
                }
            }
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


