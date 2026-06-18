package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.CuratorApiService
import com.umschool.umtasktracker.data.remote.dto.CuratorTaskDto
import com.umschool.umtasktracker.data.remote.dto.CuratorTaskReportDto
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.CuratorTaskReport
import com.umschool.umtasktracker.domain.model.SelectedFile
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.domain.repository.CuratorRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

class CuratorRepositoryImpl(
    private val api: CuratorApiService,
    private val tokenStorage: TokenStorage
) : CuratorRepository {

    override suspend fun getTasks(): Result<List<CuratorTask>> = safeApiCall {
        api.getTasks(authHeader()).map { it.toDomain() }
    }

    override suspend fun getReport(taskId: String, email: String): Result<CuratorTaskReport> =
        safeApiCall {
            api.getReport(authHeader(), taskId, email).toDomain()
        }

    override suspend fun submitReport(
        taskId: String,
        reportText: String,
        files: List<SelectedFile>,
    ): Result<Unit> = safeApiCall {
        api.submitReport(authHeader(), taskId, reportText, files)
    }

    private suspend fun authHeader(): String {
        val token = tokenStorage.getAccessToken().firstOrNull()
        require(!token.isNullOrBlank()) { "Token is null" }
        return "Bearer $token"
    }
}

private fun CuratorTaskDto.toDomain() = CuratorTask(
    id = id,
    title = title,
    description = description,
    reportTemplate = reportTemplate,
    deadline = deadline,
    created = created,
    status = TaskStatus.fromString(status),
    isCompleted = completedAt != null,
    hasReport = reportText != null,
    reportUrl = reportUrl
)

private fun CuratorTaskReportDto.toDomain() = CuratorTaskReport(
    reportText = reportText,
    fileUrls = parseFileUrls(reportUrl),
    isSubmitted = timestampEnd != null,
    submittedAt = timestampEnd,
)

private fun parseFileUrls(element: JsonElement?): List<String> {
    if (element == null) return emptyList()
    return when (element) {
        is JsonArray -> element.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
        is JsonPrimitive -> element.contentOrNull
            ?.split('\n')
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        else -> emptyList()
    }
}
