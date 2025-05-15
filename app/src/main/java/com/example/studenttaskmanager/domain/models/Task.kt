package com.example.studenttaskmanager.domain.models

data class Task(
    var id: String = "",
    var title: String = "",
    var subject: String = "",
    var professor: String = "",
    var deadline: Long = 0L,
    var priority: Priority = Priority.MEDIUM,
    var isCompleted: Boolean = false,
    var isGroupTask: Boolean = false, //личная/групповая
    var creatorId: String = "",
   // val files: List<String> = emptyList() //тут будут ссылки на файлы в Storage
){
    enum class Priority{ LOW, MEDIUM, HIGH}
}
