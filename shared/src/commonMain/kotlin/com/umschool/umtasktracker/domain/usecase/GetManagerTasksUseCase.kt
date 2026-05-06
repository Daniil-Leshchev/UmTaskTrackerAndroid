package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.domain.repository.ManagerRepository

class GetManagerTasksUseCase(
    private val repository: ManagerRepository
) {
    suspend operator fun invoke(): Result<List<ManagerTask>> {
        return repository.getTasks()
    }
}