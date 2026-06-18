package com.umschool.umtasktracker.presentation.curator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.CuratorTaskReport
import com.umschool.umtasktracker.domain.model.SelectedFile
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.usecase.GetCuratorReportUseCase
import com.umschool.umtasktracker.domain.usecase.GetCuratorTasksUseCase
import com.umschool.umtasktracker.domain.usecase.GetCurrentUserUseCase
import com.umschool.umtasktracker.domain.usecase.SubmitReportUseCase
import com.umschool.umtasktracker.ui.tasks.components.TaskStatusBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CuratorTasksViewModel(
    private val getCuratorTasks: GetCuratorTasksUseCase,
    private val getCuratorReport: GetCuratorReportUseCase,
    private val submitReportUseCase: SubmitReportUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CuratorTasksUiState())
    val uiState: StateFlow<CuratorTasksUiState> = _uiState

    private val _report = MutableStateFlow<CuratorTaskReport?>(null)
    val report: StateFlow<CuratorTaskReport?> = _report.asStateFlow()

    private val _reportLoading = MutableStateFlow(false)
    val reportLoading: StateFlow<Boolean> = _reportLoading.asStateFlow()

    private val _reportText = MutableStateFlow("")
    val reportText: StateFlow<String> = _reportText.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<SelectedFile>>(emptyList())
    val selectedFiles: StateFlow<List<SelectedFile>> = _selectedFiles.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            getCuratorTasks()
                .onSuccess { tasks ->
                    _uiState.value = _uiState.value.copy(tasks = tasks, isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки задач"
                    )
                }
        }
    }

    fun getTaskById(taskId: String): CuratorTask? =
        uiState.value.tasks.find { it.id == taskId }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterSelected(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun loadReport(taskId: String) {
        viewModelScope.launch {
            _reportLoading.value = true
            val email = getCurrentUser().getOrNull()?.email
            if (email == null) {
                _reportLoading.value = false
                _report.value = null
                return@launch
            }
            getCuratorReport(taskId, email)
                .onSuccess { _report.value = it }
                .onFailure { _report.value = null }
            _reportLoading.value = false
        }
    }

    fun resetReportDraft() {
        _reportText.value = ""
        _selectedFiles.value = emptyList()
        _submitState.value = SubmitState.Idle
        _report.value = null
    }

    fun onReportTextChange(text: String) {
        _reportText.value = text
        if (_submitState.value is SubmitState.Error) {
            _submitState.value = SubmitState.Idle
        }
    }

    fun addFiles(files: List<SelectedFile>) {
        _selectedFiles.update { current -> current + files }
    }

    fun removeFile(index: Int) {
        _selectedFiles.update { current ->
            current.toMutableList().also { if (index in it.indices) it.removeAt(index) }
        }
    }

    fun submitReport(taskId: String) {
        val text = _reportText.value
        val files = _selectedFiles.value
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            submitReportUseCase(taskId, text, files)
                .onSuccess {
                    _submitState.value = SubmitState.Success
                    _reportText.value = ""
                    _selectedFiles.value = emptyList()
                    loadReport(taskId)
                    loadTasks()
                }
                .onFailure { e ->
                    _submitState.value = SubmitState.Error(e.message ?: "Ошибка отправки")
                }
        }
    }
}

sealed class SubmitState {
    data object Idle : SubmitState()
    data object Loading : SubmitState()
    data object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
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
    val completedOnTimeCount get() = tasks.count { it.status == TaskStatus.COMPLETED }
    val inProgressCount get() = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val overdueCount get() = tasks.count { it.status == TaskStatus.OVERDUE }

    val successPercent: Int
        get() = if (tasks.isEmpty()) 0
        else (completedOnTimeCount * 100) / tasks.size
}

enum class TaskFilter {
    ALL, COMPLETED, IN_PROGRESS, OVERDUE, COMPLETED_ON_TIME
}

fun CuratorTasksUiState.toStatusBarState() = TaskStatusBarState(
    completedCount = completedCount,
    inProgressCount = inProgressCount,
    overdueCount = overdueCount,
    successPercent = successPercent,
)
