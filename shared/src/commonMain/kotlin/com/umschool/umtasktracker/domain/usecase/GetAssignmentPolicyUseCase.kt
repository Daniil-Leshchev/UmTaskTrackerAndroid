package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.AssignmentPolicy
import com.umschool.umtasktracker.domain.repository.ManagerRepository

class GetAssignmentPolicyUseCase(
    private val repository: ManagerRepository
) {
    suspend operator fun invoke(): Result<AssignmentPolicy> =
        repository.getAssignmentPolicy()
}
