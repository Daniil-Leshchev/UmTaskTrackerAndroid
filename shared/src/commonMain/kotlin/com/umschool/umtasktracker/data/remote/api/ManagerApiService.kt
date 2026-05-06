package com.umschool.umtasktracker.data.remote.api

import com.umschool.umtasktracker.data.remote.dto.FetchTasksParams
import com.umschool.umtasktracker.data.remote.dto.ManagerTaskDto
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

    private fun handleErrors(status: HttpStatusCode) {
        when {
            status.isSuccess() -> return
            status == HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized()
            status == HttpStatusCode.Forbidden -> throw ApiException.Forbidden()
            status.value in 500..599 -> throw ApiException.ServerError(status.value)
        }
    }
}