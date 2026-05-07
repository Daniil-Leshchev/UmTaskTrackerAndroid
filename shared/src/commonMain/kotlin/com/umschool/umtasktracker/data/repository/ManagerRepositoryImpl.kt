package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.ManagerApiService
import com.umschool.umtasktracker.data.remote.dto.AssignmentPolicyDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskGroupDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskIndividualDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskResponseDto
import com.umschool.umtasktracker.data.remote.dto.FetchTasksParams
import com.umschool.umtasktracker.data.remote.dto.ManagerTaskDto
import com.umschool.umtasktracker.data.remote.dto.RecipientDto
import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.model.CreateTaskParams
import com.umschool.umtasktracker.domain.model.CreateTaskResult
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.repository.ManagerRepository
import kotlinx.coroutines.flow.firstOrNull

class ManagerRepositoryImpl(
    private val api: ManagerApiService,
    private val tokenStorage: TokenStorage
) : ManagerRepository {

    override suspend fun getTasks(): Result<List<ManagerTask>> {
        return getTasks(FetchTasksParams())
    }

    suspend fun getTasks(
        params: FetchTasksParams
    ): Result<List<ManagerTask>> = safeApiCall {
        api.getTasks(authHeader(), params).map { it.toDomain() }
    }

    override suspend fun getAssignmentPolicy(): Result<AssignmentPolicy> = safeApiCall {
        api.getAssignmentPolicy(authHeader()).toDomain()
    }

    override suspend fun getRecipients(): Result<List<Recipient>> = safeApiCall {
        api.getRecipients(authHeader()).map { it.toDomain() }
    }

    override suspend fun createTask(params: CreateTaskParams): Result<CreateTaskResult> =
        safeApiCall {
            val response = when (params) {
                is CreateTaskParams.Individual ->
                    api.createTaskIndividual(authHeader(), params.toDto())
                is CreateTaskParams.Group ->
                    api.createTaskGroup(authHeader(), params.toDto())
            }
            response.toDomain()
        }

    private suspend fun authHeader(): String {
        val token = tokenStorage.getAccessToken().firstOrNull()
        require(!token.isNullOrBlank()) { "Token is null" }
        return "Bearer $token"
    }
}

private fun ManagerTaskDto.toDomain() = ManagerTask(
    id = id,
    title = title,
    description = description,
    report = report,
    deadline = deadline,
    created = created,
    status = TaskStatus.fromString(status),

    progress = progress,
    completed = completed,
    total = total,
    notCompleted = notCompleted,

    sampleCurators = sampleCurators,

    on_time = onTime,
    not_on_time = notOnTime
)

private fun AssignmentPolicyDto.toDomain() = AssignmentPolicy(
    canChooseDepartment = canChooseDepartment,
    availableRoleIds = availableRoleIds,
    subjectId = subjectId
)

private fun RecipientDto.toDomain() = Recipient(
    email = email,
    name = name,
    role = role,
    roleId = roleId,
    department = department,
    subject = subject
)

private fun CreateTaskResponseDto.toDomain() = CreateTaskResult(
    taskId = idTask,
    totalAssigned = delivery.summary.total,
    failedCount = delivery.summary.failed,
    undeliveredNames = delivery.undeliveredNamesAll
)

private fun CreateTaskParams.Individual.toDto() = CreateTaskIndividualDto(
    name = name,
    description = description,
    deadline = deadlineIso,
    report = reportFormat,
    emails = emails
)

private fun CreateTaskParams.Group.toDto() = CreateTaskGroupDto(
    name = name,
    description = description,
    deadline = deadlineIso,
    report = reportFormat,
    subjectId = subjectId,
    departmentIds = departmentIds,
    roleIds = roleIds
)
