package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.ManagerApiService
import com.umschool.umtasktracker.data.remote.dto.FetchTasksParams
import com.umschool.umtasktracker.data.remote.dto.ManagerTaskDto
import com.umschool.umtasktracker.domain.model.ManagerTask
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
        val token = tokenStorage.getAccessToken().firstOrNull()
        require(!token.isNullOrBlank()) { "Token is null" }

        api.getTasks("Bearer $token", params)
            .map { it.toDomain() }
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

    on_time  = onTime,
    not_on_time = notOnTime
)