package com.umschool.umtasktracker.presentation.curator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.CuratorTask
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
}

data class CuratorTasksUiState(
    val tasks: List<CuratorTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
