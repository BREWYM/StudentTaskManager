package com.example.studenttaskmanager.presentation.task_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.domain.repositories.TaskRepository
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private val repository: TaskRepository
) : ViewModel() {

//    var subjectPool by mutableStateOf<List<Subject>>(emptyList())
//    var showAddSubjectDialog by mutableStateOf(false)
    val subjectName = mutableStateOf("")

    private var _task = mutableStateOf<Task?>(null)
    val task: Task?
        get() = _task.value

    var newComment by mutableStateOf("")

    fun loadTask(id: String) {
        viewModelScope.launch {
            val loadedTask = repository.getTaskById(id)
            _task.value = loadedTask // Создаём пустую задачу, если не нашлось
            subjectName.value = _task.value?.subject?.name.orEmpty()
        }
    }


    fun updateTitle(title: String) {
        _task.value = task?.copy(title = title)
    }

    fun updateSubject(newName: String) {
        subjectName.value = newName.trim()
        if (newName.isNotBlank()) {
            _task.value = task?.copy(subject = Subject(name = newName.trim()))
            Log.d("UPDATE", "Subject updated: $newName")
        }
    }

    fun updateProfessor(prof: String) {
        _task.value = task?.copy(professor = prof)
    }

    fun updateDate(timestamp: String) {
        _task.value = task?.copy(deadline = timestamp.toLong())
    }
    fun updatePriority(priority: String){
        _task.value = task?.copy(priority = Task.Priority.valueOf(priority))
    }

    fun addComment() {
        val current = task ?: return
        if (newComment.isNotBlank()) {
            _task.value = current.copy(comments = current.comments + newComment)
            newComment = ""
        }
    }

    fun save() {
        viewModelScope.launch {
            _task.value?.let { task ->
                Log.d("SAVE", "Saving task: $task")
                repository.updateTask(task)
            }
        }
    }

    fun delete(onFinish: () -> Unit) {
        viewModelScope.launch {
            task?.let {
                repository.deleteTask(it.id)
                onFinish()
            }
        }
    }
}
