package com.example.studenttaskmanager.domain.repositories

import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.models.Comment
import com.example.studenttaskmanager.domain.models.Task

interface TaskRepository {
    suspend fun getTasks(userId: String): Resource<List<Task>>
    suspend fun getGroupTasks(groupId: String): Resource<List<Task>>
    suspend fun addTask(task: Task) //ID задачи
    suspend fun deleteTask(taskId: String)
    suspend fun updateTask(task: Task)

    // Комментарии и файлы
    suspend fun getComments(taskId: String): List<Comment>
    suspend fun addComment(comment: Comment)
//    suspend fun uploadFile(file: Uri): String // Возвращает URL файла
}