package com.umschool.umtasktracker.data.remote.api

import com.umschool.umtasktracker.data.remote.dto.CuratorTaskDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
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
    private fun handleErrors(status: HttpStatusCode) {
        when {
            status.isSuccess() -> return
            status == HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized()
            status == HttpStatusCode.Forbidden -> throw ApiException.Forbidden()
            status.value in 500..599 -> throw ApiException.ServerError(status.value)
        }
    }
}