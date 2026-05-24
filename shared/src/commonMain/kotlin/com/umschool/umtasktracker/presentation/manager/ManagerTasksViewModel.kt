package com.umschool.umtasktracker.presentation.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.model.ManagerTaskStatus
import com.umschool.umtasktracker.domain.model.TaskDetail
import com.umschool.umtasktracker.domain.usecase.GetManagerTasksUseCase
import com.umschool.umtasktracker.domain.usecase.GetTaskDetailsUseCase
import com.umschool.umtasktracker.ui.tasks.components.TaskStatusBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManagerTasksViewModel(
    private val getManagerTasks: GetManagerTasksUseCase,
    private val getTaskDetails: GetTaskDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerTasksUiState())
    val uiState: StateFlow<ManagerTasksUiState> = _uiState

    init {
        loadTasks()
    }

    fun loadTasks() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

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

    fun loadTaskDetails(taskId: String) {

        _uiState.value = _uiState.value.copy(
            isDetailsLoading = true
        )

        viewModelScope.launch {

            getTaskDetails(taskId)
                .onSuccess { details ->

                    _uiState.value = _uiState.value.copy(
                        taskDetails = details,
                        isDetailsLoading = false
                    )
                }
                .onFailure { e ->

                    _uiState.value = _uiState.value.copy(
                        isDetailsLoading = false,
                        error = e.message ?: "Ошибка загрузки деталей задачи"
                    )
                }
        }
    }

    fun getTaskById(taskId: String): ManagerTask? {
        return uiState.value.tasks.find { it.id == taskId }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
    }

    fun onFilterSelected(filter: ManagerTaskFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter
        )
    }
}

data class ManagerTasksUiState(
    val tasks: List<ManagerTask> = emptyList(),
    val taskDetails: List<TaskDetail> = emptyList(),

    val isLoading: Boolean = false,
    val isDetailsLoading: Boolean = false,

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
                        task.status == ManagerTaskStatus.COMPLETED

                    ManagerTaskFilter.IN_PROGRESS ->
                        task.status == ManagerTaskStatus.IN_PROGRESS

                    ManagerTaskFilter.NOT_STARTED ->
                        task.status == ManagerTaskStatus.NOT_STARTED
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
        get() = tasks.count {
            it.status == ManagerTaskStatus.NOT_STARTED
        }

    val successPercent: Int
        get() {

            val total = tasks.sumOf {
                (it.total).toInt()
            }

            val onTime = tasks.sumOf {
                (it.on_time ?: 0).toInt()
            }

            return if (total == 0) {
                0
            } else {
                (onTime * 100) / total
            }
        }
}

enum class ManagerTaskFilter {
    ALL,
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED
}

fun ManagerTasksUiState.toStatusBarState() = TaskStatusBarState(
    completedCount = completedTasks,
    inProgressCount = inProgressTasks,
    overdueCount = overdueTasks,
    successPercent = successPercent,
)