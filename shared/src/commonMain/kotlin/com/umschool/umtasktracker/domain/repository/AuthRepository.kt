package com.umschool.umtasktracker.domain.repository

import com.umschool.umtasktracker.domain.model.AuthToken
import com.umschool.umtasktracker.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthToken>
    suspend fun getProfile(accessToken: String): Result<UserProfile>
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        roleId: Int,
        subjectId: Int,
        departmentId: Int
    ): Result<Unit>
}
