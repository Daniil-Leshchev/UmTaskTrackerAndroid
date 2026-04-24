package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.remote.api.CuratorApiService
import com.umschool.umtasktracker.data.remote.dto.TaskDto
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.repository.CuratorRepository

class CuratorRepositoryImpl(
    private val api: CuratorApiService
) : CuratorRepository {

    override suspend fun getTasks(): Result<List<CuratorTask>> = safeApiCall {
        api.getTasks().map { it.toDomain() }
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
    isCompleted = completedAt,
    hasReport = reportText,
    reportUrl = reportUrl
)
