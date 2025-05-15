package com.example.studenttaskmanager.presentation.task_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.use_cases.GetTasksUseCase
import kotlinx.coroutines.launch

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
                        tasks = res.data?: emptyList(),
                        isLoading = false
                    )
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

}