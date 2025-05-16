package com.example.studenttaskmanager.presentation.task_list

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.common.toLocalDate
import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.domain.use_cases.GetTasksUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _state = mutableStateOf(TaskListState())
    val state: State<TaskListState> = _state


    fun loadTasks(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val res = getTasksUseCase(userId)) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        allTasks = res.data?: emptyList(),
                        actualTasks = res.data?: emptyList(),
                        isLoading = false
                    )
                        applyFilters()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = res.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun applyFilters() {
        val current = _state.value
        Log.d("Filter","${current.actualTasks}")

        val filtered = current.allTasks.filter { task ->
            val matchesQuery = current.query.isBlank() ||
                    task.title.contains(current.query, ignoreCase = true) ||
                    task.subject.toString().contains(current.query, ignoreCase = true) == true
            val matchesStatus = current.statusFilter?.let {
                task.priority == it
            } != false

            val matchesProfessor = current.professorFilter?.let {
                task.professor.contains(it, ignoreCase = true)
            } != false

            val matchesSubject = current.subjectFilter?.let {
                task.subject.toString() == it.name
            } != false

            val matchesDate = current.dateFilter?.let { filterDate ->
                task.deadline.toLocalDate() == filterDate
            } != false

            matchesQuery && matchesStatus && matchesProfessor && matchesSubject && matchesDate
        }
        Log.d("Filter","$filtered")
        Log.d("Filter","${_state.value.actualTasks}")

        val today = LocalDate.now()

        _state.value = _state.value.copy(
            actualTasks = filtered.filter {!it.deadline.toLocalDate().isBefore(today)},
            archivedTasks = filtered.filter { it.deadline.toLocalDate().isBefore(today) == true }
        )
        Log.d("Filter","${_state.value.actualTasks}")
    }

    fun onQueryChanged(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
        applyFilters()
    }

    fun onStatusFilterChanged(priority: Task.Priority?) {
        _state.value = _state.value.copy(statusFilter = priority)
        applyFilters()
    }

    fun onProfessorFilterChanged(professor: String?) {
        _state.value = _state.value.copy(professorFilter = professor)
        applyFilters()
    }

    fun onDateFilterChanged(date: LocalDate?) {
        _state.value = _state.value.copy(dateFilter = date)
        applyFilters()
    }

    fun onSubjectFilterChanged(subject: Subject?) {
        _state.value = _state.value.copy(subjectFilter = subject)
        applyFilters()
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            query = "",
            statusFilter = null,
            professorFilter = null,
            dateFilter = null,
            subjectFilter = null
        )
        applyFilters()
    }

}
