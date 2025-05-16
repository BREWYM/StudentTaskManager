package com.example.studenttaskmanager.presentation.add_task

import com.example.studenttaskmanager.domain.models.Task

data class AddTaskState(
    val title: String = "",
    val subject: String? = null,
    val deadline: Long = System.currentTimeMillis(),
    val professor: String = "",
    val priority: Task.Priority = Task.Priority.MEDIUM,
    val isGroupTask: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
