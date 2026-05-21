package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.UserProfile
import com.umschool.umtasktracker.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserProfile> = repository.getCurrentUser()
}
