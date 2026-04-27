package com.umschool.umtasktracker.presentation.curator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.usecase.GetCuratorTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CuratorTasksViewModel(
    private val getCuratorTasks: GetCuratorTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CuratorTasksUiState())
    val uiState: StateFlow<CuratorTasksUiState> = _uiState

    init {
        loadTasks()
    }

    fun loadTasks() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            getCuratorTasks()
                .onSuccess { tasks ->
                    _uiState.value = _uiState.value.copy(
                        tasks = tasks,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки задач"
                    )
                }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterSelected(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}

data class CuratorTasksUiState(
    val tasks: List<CuratorTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val selectedFilter: TaskFilter = TaskFilter.ALL
) {
    val filteredTasks: List<CuratorTask>
        get() = tasks
            .filter { task ->
                when (selectedFilter) {
                    TaskFilter.ALL -> true

                    TaskFilter.COMPLETED ->
                        task.status == TaskStatus.COMPLETED || task.status == TaskStatus.COMPLETED_LATE

                    TaskFilter.IN_PROGRESS ->
                        task.status == TaskStatus.IN_PROGRESS

                    TaskFilter.OVERDUE ->
                        task.status == TaskStatus.OVERDUE

                    TaskFilter.COMPLETED_ON_TIME ->
                        task.status == TaskStatus.COMPLETED
                }
            }
            .filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }

    val completedCount get() = tasks.count { it.status == TaskStatus.COMPLETED || it.status == TaskStatus.COMPLETED_LATE }
    val completedOnTimeCount get() = tasks.count { it.status == TaskStatus.COMPLETED}
    val inProgressCount get() = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val overdueCount get() = tasks.count { it.status == TaskStatus.OVERDUE}

    val successPercent: Int
        get() = if (tasks.isEmpty()) 0
        else (completedOnTimeCount * 100) / tasks.size
}

enum class TaskFilter {
    ALL, COMPLETED, IN_PROGRESS, OVERDUE, COMPLETED_ON_TIME
}
