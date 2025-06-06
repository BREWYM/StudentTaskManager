package com.example.studenttaskmanager.presentation.add_task

import com.example.studenttaskmanager.domain.models.Subject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.domain.use_cases.AddTaskUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AddTaskState())
    val state: State<AddTaskState> = _state

    // Обновление полей
    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updateSubject(subject: String) {
        _state.value = _state.value.copy(subject = subject)
    }

    fun updateProfessor(professor: String) {
        _state.value = _state.value.copy(professor = professor)
    }

    fun updateDeadline(deadline: Long) {
        _state.value = _state.value.copy(deadline = deadline)
    }

    fun updatePriority(priority: Task.Priority) {
        _state.value = _state.value.copy(priority = priority)
    }

    fun toggleGroupTask() {
        _state.value = _state.value.copy(isGroupTask = !_state.value.isGroupTask)
    }

    // Отправка задачи
    fun addTask() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val task = Task(
                    title = _state.value.title,
                    subject = Subject(
                        name = _state.value.subject?:"",
                        professor = _state.value.professor,
                        groupId = "123"
                    ),
                    professor = _state.value.professor,
                    deadline = _state.value.deadline,
                    priority = _state.value.priority,
                    isGroupTask = _state.value.isGroupTask,
                    creatorId = Firebase.auth.currentUser?.uid ?: ""
                )
                addTaskUseCase(task)
                _state.value = _state.value.copy(isSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}