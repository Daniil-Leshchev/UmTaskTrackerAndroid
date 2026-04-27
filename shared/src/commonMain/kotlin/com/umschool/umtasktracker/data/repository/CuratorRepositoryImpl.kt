package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.CuratorApiService
import com.umschool.umtasktracker.data.remote.dto.TaskDto
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.repository.CuratorRepository
import kotlinx.coroutines.flow.firstOrNull

class CuratorRepositoryImpl(
    private val api: CuratorApiService,
    private val tokenStorage: TokenStorage
) : CuratorRepository {

    override suspend fun getTasks(): Result<List<CuratorTask>> = safeApiCall {
        val token = tokenStorage.getAccessToken().firstOrNull()

        require(!token.isNullOrBlank()) { "Token is null" }

        api.getTasks("Bearer $token").map { it.toDomain() }
    }
}

private fun TaskDto.toDomain() = CuratorTask(
    id = id,
    title = title,
    description = description,
    reportTemplate = reportTemplate,
    deadline = deadline,
    created = created,
    status = TaskStatus.fromString(status),
    isCompleted = completedAt == true,
    hasReport = reportText == true,
    reportUrl = reportUrl
)
