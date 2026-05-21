package com.umschool.umtasktracker.presentation.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.domain.usecase.CreateTaskUseCase
import com.umschool.umtasktracker.domain.usecase.GetAssignmentPolicyUseCase
import com.umschool.umtasktracker.domain.usecase.GetCurrentUserUseCase
import com.umschool.umtasktracker.domain.usecase.GetRecipientsUseCase
import com.umschool.umtasktracker.domain.usecase.LoadCatalogsUseCase
import com.umschool.umtasktracker.presentation.util.DateFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val VISIBLE_CURATOR_ROLE_IDS = setOf(1, 2, 3)

class CreateTaskViewModel(
    private val getPolicy: GetAssignmentPolicyUseCase,
    private val getRecipients: GetRecipientsUseCase,
    private val loadCatalogs: LoadCatalogsUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
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
            val catalogsDeferred = async { loadCatalogs() }
            val userDeferred = async { getCurrentUser() }

            val policyResult = policyDeferred.await()
            val recipientsResult = recipientsDeferred.await()
            val catalogsResult = catalogsDeferred.await()
            val userResult = userDeferred.await()

            if (policyResult.isFailure || recipientsResult.isFailure ||
                catalogsResult.isFailure || userResult.isFailure
            ) {
                _uiState.update {
                    it.copy(
                        isLoadingInit = false,
                        error = policyResult.exceptionOrNull()?.message
                            ?: recipientsResult.exceptionOrNull()?.message
                            ?: catalogsResult.exceptionOrNull()?.message
                            ?: userResult.exceptionOrNull()?.message
                            ?: "Ошибка загрузки данных"
                    )
                }
                return@launch
            }

            val policy = policyResult.getOrThrow()
            val recipients = recipientsResult.getOrThrow()
            val catalogs = catalogsResult.getOrThrow()
            val profile = userResult.getOrThrow()

            val availableRoles = catalogs.roles
                .filter { it.id in VISIBLE_CURATOR_ROLE_IDS }
                .let { rolesByIdAllowList ->
                    if (policy.availableRoleIds.isEmpty()) rolesByIdAllowList
                    else rolesByIdAllowList.filter { it.id in policy.availableRoleIds }
                }

            _uiState.update {
                it.copy(
                    isLoadingInit = false,
                    policy = policy,
                    canChooseDepartment = profile.isAdmin,
                    recipients = recipients,
                    filteredRecipients = recipients,
                    availableDepartments = catalogs.departments,
                    availableRoles = availableRoles
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

    fun toggleDepartment(id: Int) {
        _uiState.update { state ->
            val updated = if (id in state.selectedDepartmentIds)
                state.selectedDepartmentIds - id
            else
                state.selectedDepartmentIds + id
            state.copy(selectedDepartmentIds = updated, validationError = null)
        }
    }

    fun toggleRole(id: Int) {
        _uiState.update { state ->
            val updated = if (id in state.selectedRoleIds)
                state.selectedRoleIds - id
            else
                state.selectedRoleIds + id
            state.copy(selectedRoleIds = updated, validationError = null)
        }
    }

    fun toggleSpecificCuratorMode() {
        _uiState.update {
            it.copy(specificCuratorMode = !it.specificCuratorMode, validationError = null)
        }
    }

    fun onTaskNameChange(value: String) =
        _uiState.update { it.copy(taskName = value, validationError = null) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value, validationError = null) }

    fun onDeadlineDateChange(dateUtcMillis: Long?) =
        _uiState.update { it.copy(deadlineDateMillis = dateUtcMillis, validationError = null) }

    fun onDeadlineTimeChange(hour: Int, minute: Int) =
        _uiState.update {
            it.copy(deadlineHour = hour, deadlineMinute = minute, validationError = null)
        }

    fun onReportFormatChange(value: String) =
        _uiState.update { it.copy(reportFormat = value, validationError = null) }

    fun validateStep1(): Boolean {
        val state = _uiState.value
        val message = when {
            state.taskName.isBlank() -> "Введите название задачи"
            state.description.isBlank() -> "Введите описание задачи"
            state.reportFormat.isBlank() -> "Укажите форму отчёта"
            state.deadlineDateMillis == null || state.deadlineHour == null || state.deadlineMinute == null ->
                "Укажите дату и время дедлайна"
            else -> null
        }
        if (message != null) {
            _uiState.update { it.copy(validationError = message) }
            return false
        }
        _uiState.update { it.copy(validationError = null) }
        return true
    }

    fun submitTask() {
        val state = _uiState.value

        val deadlineDate = state.deadlineDateMillis
        val deadlineHour = state.deadlineHour
        val deadlineMinute = state.deadlineMinute
        val policy = state.policy

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
            deadlineDate == null || deadlineHour == null || deadlineMinute == null -> {
                _uiState.update { it.copy(validationError = "Укажите дату и время дедлайна") }
                return
            }
            state.specificCuratorMode && state.selectedRecipients.isEmpty() -> {
                _uiState.update { it.copy(validationError = "Выберите хотя бы одного исполнителя") }
                return
            }
            !state.specificCuratorMode && state.selectedRoleIds.isEmpty() -> {
                _uiState.update { it.copy(validationError = "Выберите хотя бы одну группу кураторов") }
                return
            }
            !state.specificCuratorMode && state.canChooseDepartment && state.selectedDepartmentIds.isEmpty() -> {
                _uiState.update { it.copy(validationError = "Выберите хотя бы одно направление") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, validationError = null, error = null) }

            val deadlineIso = DateFormatter.composeMoscowIso(
                dateUtcMillis = deadlineDate!!,
                hour = deadlineHour!!,
                minute = deadlineMinute!!
            )

            val params: CreateTaskParams = if (state.specificCuratorMode) {
                CreateTaskParams.Individual(
                    name = state.taskName.trim(),
                    description = state.description.trim(),
                    deadlineIso = deadlineIso,
                    reportFormat = state.reportFormat.trim(),
                    emails = state.selectedRecipients.map { it.email }
                )
            } else {
                CreateTaskParams.Group(
                    name = state.taskName.trim(),
                    description = state.description.trim(),
                    deadlineIso = deadlineIso,
                    reportFormat = state.reportFormat.trim(),
                    subjectId = policy?.subjectId ?: 0,
                    departmentIds = state.selectedDepartmentIds.toList(),
                    roleIds = state.selectedRoleIds.toList()
                )
            }

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
    val canChooseDepartment: Boolean = false,
    val availableDepartments: List<CatalogItem> = emptyList(),
    val availableRoles: List<CatalogItem> = emptyList(),

    val searchQuery: String = "",
    val filteredRecipients: List<Recipient> = emptyList(),
    val selectedRecipients: List<Recipient> = emptyList(),

    val selectedDepartmentIds: Set<Int> = emptySet(),
    val selectedRoleIds: Set<Int> = emptySet(),
    val specificCuratorMode: Boolean = false,

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
