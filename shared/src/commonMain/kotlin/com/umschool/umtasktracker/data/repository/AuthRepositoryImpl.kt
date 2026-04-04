package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.AuthApiService
import com.umschool.umtasktracker.data.remote.dto.LoginRequest
import com.umschool.umtasktracker.data.remote.dto.RegisterRequest
import com.umschool.umtasktracker.domain.model.AuthToken
import com.umschool.umtasktracker.domain.model.UserProfile
import com.umschool.umtasktracker.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthToken> = safeApiCall {
        val response = apiService.login(LoginRequest(email, password))
        tokenStorage.saveTokens(response.access, response.refresh)
        AuthToken(access = response.access, refresh = response.refresh)
    }

    override suspend fun getProfile(accessToken: String): Result<UserProfile> = safeApiCall {
        val dto = apiService.getProfile(accessToken)
        UserProfile(
            email = dto.email,
            name = "${dto.firstName} ${dto.lastName}".trim(),
            isAdmin = dto.isAdmin,
            roleName = dto.role
        )
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ): Result<Unit> = safeApiCall {
        apiService.register(
            RegisterRequest(
                email = email,
                password = password,
                name = "$firstName $lastName".trim(),
                firstName = firstName,
                lastName = lastName,
                roleId = roleId,
                subjectId = subjectId,
                departmentId = departmentId
            )
        )
    }
}
