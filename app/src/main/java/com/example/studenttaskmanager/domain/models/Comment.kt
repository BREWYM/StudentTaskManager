package com.example.studenttaskmanager.domain.models

data class Comment(
    val id: String = "",
    val taskId: String, // К какой задаче относится
    val authorId: String, // ID автора
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachments: List<String> = emptyList() // Ссылки на файлы
)