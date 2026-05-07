package com.umschool.umtasktracker.presentation.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.domain.usecase.CreateTaskUseCase
import com.umschool.umtasktracker.domain.usecase.GetAssignmentPolicyUseCase
import com.umschool.umtasktracker.domain.usecase.GetRecipientsUseCase
import com.umschool.umtasktracker.presentation.util.DateFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val getPolicy: GetAssignmentPolicyUseCase,
    private val getRecipients: GetRecipientsUseCase,
    private val createTask: CreateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(isLoadingInit = true, error = null) }

        viewModelScope.launch {
            val policyDeferred = async { getPolicy() }
            val recipientsDeferred = async { getRecipients() }

            val policyResult = policyDeferred.await()
            val recipientsResult = recipientsDeferred.await()

            if (policyResult.isFailure || recipientsResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoadingInit = false,
                        error = policyResult.exceptionOrNull()?.message
                            ?: recipientsResult.exceptionOrNull()?.message
                            ?: "Ошибка загрузки данных"
                    )
                }
                return@launch
            }

            val recipients = recipientsResult.getOrThrow()
            _uiState.update {
                it.copy(
                    isLoadingInit = false,
                    policy = policyResult.getOrThrow(),
                    recipients = recipients,
                    filteredRecipients = recipients
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.recipients
            else state.recipients.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredRecipients = filtered)
        }
    }

    fun toggleRecipient(recipient: Recipient) {
        _uiState.update { state ->
            val current = state.selectedRecipients
            val updated = if (current.any { it.email == recipient.email })
                current.filter { it.email != recipient.email }
            else
                current + recipient
            state.copy(selectedRecipients = updated)
        }
    }

    fun removeRecipient(email: String) {
        _uiState.update { state ->
            state.copy(selectedRecipients = state.selectedRecipients.filter { it.email != email })
        }
    }

    fun onTaskNameChange(value: String) =
        _uiState.update { it.copy(taskName = value, validationError = null) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    fun onDeadlineDateChange(dateUtcMillis: Long?) =
        _uiState.update { it.copy(deadlineDateMillis = dateUtcMillis, validationError = null) }

    fun onDeadlineTimeChange(hour: Int, minute: Int) =
        _uiState.update {
            it.copy(deadlineHour = hour, deadlineMinute = minute, validationError = null)
        }

    fun onReportFormatChange(value: String) =
        _uiState.update { it.copy(reportFormat = value) }

    fun submitTask() {
        val state = _uiState.value

        val deadlineDate = state.deadlineDateMillis
        val deadlineHour = state.deadlineHour
        val deadlineMinute = state.deadlineMinute
        when {
            state.taskName.isBlank() -> {
                _uiState.update { it.copy(validationError = "Введите название задачи") }
                return
            }
            state.description.isBlank() -> {
                _uiState.update { it.copy(validationError = "Введите описание задачи") }
                return
            }
            state.reportFormat.isBlank() -> {
                _uiState.update { it.copy(validationError = "Укажите форму отчёта") }
                return
            }
            state.selectedRecipients.isEmpty() -> {
                _uiState.update { it.copy(validationError = "Выберите хотя бы одного исполнителя") }
                return
            }
            deadlineDate == null || deadlineHour == null || deadlineMinute == null -> {
                _uiState.update { it.copy(validationError = "Укажите дату и время дедлайна") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, validationError = null, error = null) }

            val params = CreateTaskParams.Individual(
                name = state.taskName.trim(),
                description = state.description.trim(),
                deadlineIso = DateFormatter.composeMoscowIso(
                    dateUtcMillis = deadlineDate!!,
                    hour = deadlineHour!!,
                    minute = deadlineMinute!!
                ),
                reportFormat = state.reportFormat.trim(),
                emails = state.selectedRecipients.map { it.email }
            )

            createTask(params)
                .onSuccess { result ->
                    _uiState.update { it.copy(isSubmitting = false, submitSuccess = result) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = e.message ?: "Ошибка создания задачи"
                        )
                    }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class CreateTaskUiState(
    val isLoadingInit: Boolean = true,
    val recipients: List<Recipient> = emptyList(),
    val policy: AssignmentPolicy? = null,

    val searchQuery: String = "",
    val filteredRecipients: List<Recipient> = emptyList(),
    val selectedRecipients: List<Recipient> = emptyList(),

    val taskName: String = "",
    val description: String = "",
    val deadlineDateMillis: Long? = null,
    val deadlineHour: Int? = null,
    val deadlineMinute: Int? = null,
    val reportFormat: String = "",

    val isSubmitting: Boolean = false,
    val submitSuccess: CreateTaskResult? = null,

    val error: String? = null,
    val validationError: String? = null
)
