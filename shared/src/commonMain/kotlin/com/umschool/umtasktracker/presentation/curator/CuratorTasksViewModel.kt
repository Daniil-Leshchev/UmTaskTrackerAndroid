package com.umschool.umtasktracker.presentation.curator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.data.remote.dto.TaskDto
import com.umschool.umtasktracker.data.repository.CuratorRepositoryImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CuratorTasksViewModel(
    private val repository: CuratorRepositoryImpl
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                _tasks.value = repository.getTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}