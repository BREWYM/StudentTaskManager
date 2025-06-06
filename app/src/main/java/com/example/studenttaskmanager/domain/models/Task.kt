package com.example.studenttaskmanager.domain.models

import com.example.studenttaskmanager.domain.models.Task.Priority

data class Task(
    var id: String = "",
    var title: String = "",
    var subject: Subject? = null,
    var professor: String = "",
    var deadline: Long = 0L,
    var priority: Priority = Priority.MEDIUM,
    var isCompleted: Boolean = false,
    var isGroupTask: Boolean = false, //личная/групповая
    var creatorId: String = "",
    val comments: List<String> = emptyList()
   // val files: List<String> = emptyList() //тут будут ссылки на файлы в Storage
){
    enum class Priority{ LOW, MEDIUM, HIGH}
}

data class TaskDto(
    var id: String = "",
    var title: String = "",
    var subjectName: String = "",
    var professor: String = "",
    var deadline: Long = 0L,
    var priority: String = Priority.MEDIUM.name,
    var isCompleted: Boolean = false,
    var isGroupTask: Boolean = false,
    var creatorId: String = "",
    val comments: List<String> = emptyList()
    // files можно добавить позже
){
    fun toTask(subject: Subject?): Task {
        return Task(
            id = id,
            title = title,
            subject = subject,
            professor = professor,
            deadline = deadline,
            priority = Priority.valueOf(priority),
            isCompleted = isCompleted,
            isGroupTask = isGroupTask,
            creatorId = creatorId
        )
    }
    fun toPersonalTask(subjectName: String): Task {
        return Task(
            id = id,
            title = title,
            subject = Subject(name = subjectName),
            professor = professor,
            deadline = deadline,
            priority = Priority.valueOf(priority),
            isCompleted = isCompleted,
            isGroupTask = isGroupTask,
            creatorId = creatorId
        )
    }

    companion object {
        fun fromTask(task: Task): TaskDto {
            return TaskDto(
                id = task.id,
                title = task.title,
                subjectName = task.subject?.name ?: "",
                professor = task.professor,
                deadline = task.deadline,
                priority = task.priority.name,
                isCompleted = task.isCompleted,
                isGroupTask = task.isGroupTask,
                creatorId = task.creatorId
            )
        }
    }
}

