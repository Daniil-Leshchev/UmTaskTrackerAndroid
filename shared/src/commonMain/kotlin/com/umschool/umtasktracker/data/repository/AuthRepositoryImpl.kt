package com.umschool.umtasktracker.data.repository

import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.data.remote.api.ApiException
import com.umschool.umtasktracker.data.remote.api.AuthApiService
import com.umschool.umtasktracker.data.remote.dto.LoginRequest
import com.umschool.umtasktracker.domain.model.AuthToken
import com.umschool.umtasktracker.domain.model.UserProfile
import com.umschool.umtasktracker.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthToken> =
        runCatching {
            val response = apiService.login(LoginRequest(email, password))
            tokenStorage.saveTokens(response.access, response.refresh)
            AuthToken(access = response.access, refresh = response.refresh)
        }.recoverCatching { throwable ->
            println("LOGIN ERROR: ${throwable::class.simpleName}: ${throwable.message}")
            throwable.printStackTrace()
            when (throwable) {
                is ApiException -> throw throwable
                else -> throw ApiException.NetworkError(throwable)
            }
        }

    override suspend fun getProfile(accessToken: String): Result<UserProfile> =
        runCatching {
            val dto = apiService.getProfile(accessToken)
            UserProfile(
                email = dto.email,
                name = "${dto.firstName} ${dto.lastName}".trim(),
                isAdmin = dto.isAdmin,
                roleName = dto.role
            )
        }.recoverCatching { throwable ->
            println("PROFILE ERROR: ${throwable::class.simpleName}: ${throwable.message}")
            when (throwable) {
                is ApiException -> throw throwable
                else -> throw ApiException.NetworkError(throwable)
            }
        }
}
