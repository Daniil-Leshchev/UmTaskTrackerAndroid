package com.umschool.umtasktracker.data.remote.api

import com.umschool.umtasktracker.data.remote.dto.AssignmentPolicyDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskGroupDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskIndividualDto
import com.umschool.umtasktracker.data.remote.dto.CreateTaskResponseDto
import com.umschool.umtasktracker.data.remote.dto.FetchTasksParams
import com.umschool.umtasktracker.data.remote.dto.ManagerTaskDto
import com.umschool.umtasktracker.data.remote.dto.RecipientDto
import com.umschool.umtasktracker.data.remote.dto.TaskDetailDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ManagerApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun getTasks(
        token: String,
        params: FetchTasksParams = FetchTasksParams()
    ): List<ManagerTaskDto> {

        val response = httpClient.get("$baseUrl/api/tasks/") {
            header(HttpHeaders.Authorization, token)

            url {
                parameters.append("scope", params.scope)

                params.subjectId?.let {
                    parameters.append("subject_id", it)
                }
                params.departmentId?.let {
                    parameters.append("department_id", it)
                }
                params.status?.let {
                    parameters.append("status", it)
                }
                params.query?.let {
                    parameters.append("q", it)
                }
            }
        }

        handleErrors(response.status)
        return response.body()
    }

    suspend fun getAssignmentPolicy(token: String): AssignmentPolicyDto {
        val response = httpClient.get("$baseUrl/api/tasks/assignment-policy/") {
            header(HttpHeaders.Authorization, token)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getRecipients(token: String): List<RecipientDto> {
        val response = httpClient.get("$baseUrl/api/tasks/recipients/") {
            header(HttpHeaders.Authorization, token)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun createTaskIndividual(
        token: String,
        dto: CreateTaskIndividualDto
    ): CreateTaskResponseDto {
        val response = httpClient.post("$baseUrl/api/tasks/") {
            header(HttpHeaders.Authorization, token)
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun createTaskGroup(
        token: String,
        dto: CreateTaskGroupDto
    ): CreateTaskResponseDto {
        val response = httpClient.post("$baseUrl/api/tasks/") {
            header(HttpHeaders.Authorization, token)
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getTaskDetails(
        token: String,
        taskId: String
    ): List<TaskDetailDto> {

        val response = httpClient.get("$baseUrl/api/tasks/$taskId/") {
            header(HttpHeaders.Authorization, token)
        }

        handleErrors(response.status)

        return response.body()
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
