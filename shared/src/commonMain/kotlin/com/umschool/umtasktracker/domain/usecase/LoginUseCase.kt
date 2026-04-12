package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.LoginResult
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {

        val loginResult = repository.login(email, password)

        return loginResult.fold(
            onSuccess = { token ->

                val profileResult = repository.getProfile(token.access)

                profileResult.map { profile ->

                    val role = UserRole.fromProfile(profile)

                    LoginResult(
                        role = role,
                        isApproved = profile.isApproved
                    )
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}
