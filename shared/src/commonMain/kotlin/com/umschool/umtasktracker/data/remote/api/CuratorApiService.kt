package com.umschool.umtasktracker.data.remote.api

import com.umschool.umtasktracker.data.remote.dto.CuratorTaskDto
import com.umschool.umtasktracker.data.remote.dto.CuratorTaskReportDto
import com.umschool.umtasktracker.domain.model.SelectedFile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.*

class CuratorApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun getTasks(token: String): List<CuratorTaskDto> {
        val response = httpClient.get("$baseUrl/api/tasks/my/") {
            header(HttpHeaders.Authorization, token)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getReport(
        token: String,
        taskId: String,
        email: String,
    ): CuratorTaskReportDto {
        val response = httpClient.get("$baseUrl/api/tasks/reports/$taskId/$email/") {
            header(HttpHeaders.Authorization, token)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun submitReport(
        token: String,
        taskId: String,
        reportText: String,
        files: List<SelectedFile>,
    ) {
        val response = httpClient.post("$baseUrl/api/tasks/reports/$taskId/submit/") {
            header(HttpHeaders.Authorization, token)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        if (reportText.isNotBlank()) {
                            append("report_text", reportText)
                        }
                        files.forEach { file ->
                            append(
                                key = "files",
                                value = file.bytes,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${file.name}\""
                                    )
                                    append(HttpHeaders.ContentType, file.mimeType)
                                }
                            )
                        }
                    }
                )
            )
        }
        handleErrors(response.status)
    }

    private fun handleErrors(status: HttpStatusCode) {
        when {
            status.isSuccess() -> return
            status == HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized()
            status == HttpStatusCode.Forbidden -> throw ApiException.Forbidden()
            status.value in 500..599 -> throw ApiException.ServerError(status.value)
        }
    }
}
