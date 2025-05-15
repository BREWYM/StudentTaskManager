package com.example.studenttaskmanager.domain.use_cases

import com.example.studenttaskmanager.domain.repositories.TaskRepository

class GetTasksUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String) = taskRepository.getTasks(userId)

}