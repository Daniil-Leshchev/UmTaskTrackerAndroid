package com.umschool.umtasktracker.data.remote.api

import com.umschool.umtasktracker.data.remote.dto.DepartmentDto
import com.umschool.umtasktracker.data.remote.dto.LoginRequest
import com.umschool.umtasktracker.data.remote.dto.RegisterRequest
import com.umschool.umtasktracker.data.remote.dto.RoleDto
import com.umschool.umtasktracker.data.remote.dto.SubjectDto
import com.umschool.umtasktracker.data.remote.dto.TokenResponse
import com.umschool.umtasktracker.data.remote.dto.UserProfileDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AuthApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun login(request: LoginRequest): TokenResponse {
        val response = httpClient.post("$baseUrl/api/login/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getProfile(accessToken: String): UserProfileDto {
        val response = httpClient.get("$baseUrl/api/users/me/") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        handleErrors(response.status)
        return response.body()
    }

    suspend fun register(request: RegisterRequest) {
        val response = httpClient.post("$baseUrl/api/register/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Exception) { "" }
            when {
                response.status == HttpStatusCode.Conflict ||
                    response.status.value == 400 && body.contains("email", ignoreCase = true)
                    -> throw ApiException.EmailAlreadyExists()
                response.status == HttpStatusCode.BadRequest
                    -> throw ApiException.ValidationError(body)
                else -> handleErrors(response.status)
            }
        }
    }

    suspend fun getRoles(): List<RoleDto> {
        val response = httpClient.get("$baseUrl/api/catalogs/roles/")
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getSubjects(): List<SubjectDto> {
        val response = httpClient.get("$baseUrl/api/catalogs/subjects/")
        handleErrors(response.status)
        return response.body()
    }

    suspend fun getDepartments(): List<DepartmentDto> {
        val response = httpClient.get("$baseUrl/api/catalogs/departments/")
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
