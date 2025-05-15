package com.example.studenttaskmanager.domain.use_cases

import com.example.studenttaskmanager.domain.repositories.TaskRepository
import com.example.studenttaskmanager.domain.models.Task

class AddTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
       return taskRepository.addTask(task)
    }
}