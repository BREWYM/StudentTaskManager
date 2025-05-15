package com.example.studenttaskmanager.presentation.task_list

import com.example.studenttaskmanager.domain.models.Task

data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)