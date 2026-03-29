package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<UserRole> {
        val tokenResult = repository.login(email, password)
        val token = tokenResult.getOrElse { return Result.failure(it) }

        val profileResult = repository.getProfile(token.access)
        val profile = profileResult.getOrElse { return Result.failure(it) }

        return Result.success(UserRole.fromProfile(profile))
    }
}
