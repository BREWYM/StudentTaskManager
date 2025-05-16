package com.example.studenttaskmanager.presentation.task_list

import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.Task
import java.time.LocalDate

data class TaskListState(
    val allTasks: List<Task> = emptyList(),
    val actualTasks: List<Task> = emptyList(),
    val archivedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val statusFilter: Task.Priority? = null,
    val professorFilter: String? = null,
    val dateFilter: LocalDate? = null,
    val subjectFilter: Subject? = null

)