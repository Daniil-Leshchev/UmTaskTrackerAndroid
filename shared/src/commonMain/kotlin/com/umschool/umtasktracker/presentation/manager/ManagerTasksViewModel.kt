package com.umschool.umtasktracker.presentation.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.usecase.GetManagerTasksUseCase
import com.umschool.umtasktracker.ui.tasks.components.TaskStatusBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManagerTasksViewModel(
    private val getManagerTasks: GetManagerTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerTasksUiState())
    val uiState: StateFlow<ManagerTasksUiState> = _uiState

    init {
        loadTasks()
    }

    fun loadTasks() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            getManagerTasks()
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

    fun onFilterSelected(filter: ManagerTaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}

data class ManagerTasksUiState(
    val tasks: List<ManagerTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val searchQuery: String = "",
    val selectedFilter: ManagerTaskFilter = ManagerTaskFilter.ALL
) {

    val filteredTasks: List<ManagerTask>
        get() = tasks
            .filter { task ->
                when (selectedFilter) {
                    ManagerTaskFilter.ALL -> true

                    ManagerTaskFilter.COMPLETED ->
                        task.status == TaskStatus.COMPLETED ||
                                task.status == TaskStatus.COMPLETED_LATE

                    ManagerTaskFilter.IN_PROGRESS ->
                        task.status == TaskStatus.IN_PROGRESS

                    ManagerTaskFilter.OVERDUE ->
                        task.status == TaskStatus.OVERDUE

                    ManagerTaskFilter.COMPLETED_ON_TIME ->
                        task.status == TaskStatus.COMPLETED
                }
            }
            .filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
    val completedTasks
        get() = tasks.sumOf { (it.completed).toInt() }

    val inProgressTasks
        get() = tasks.sumOf {
            (it.total).toInt() - (it.completed).toInt()
        }

    val overdueTasks
        get() = tasks.count { it.status == TaskStatus.OVERDUE }

    val successPercent: Int
        get() {
            val total = tasks.sumOf { (it.total).toInt() }
            val onTime = tasks.sumOf { (it.on_time ?: 0).toInt() }

            return if (total == 0) 0 else (onTime * 100) / total
        }
}

enum class ManagerTaskFilter {
    ALL, COMPLETED, IN_PROGRESS, OVERDUE, COMPLETED_ON_TIME
}

fun ManagerTasksUiState.toStatusBarState() = TaskStatusBarState(
    completedCount = completedTasks,
    inProgressCount = inProgressTasks,
    overdueCount = overdueTasks,
    successPercent = successPercent,
)