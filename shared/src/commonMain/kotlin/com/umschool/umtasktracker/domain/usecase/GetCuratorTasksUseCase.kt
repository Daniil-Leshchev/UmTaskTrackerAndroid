package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.repository.CuratorRepository

class GetCuratorTasksUseCase(
    private val repository: CuratorRepository
) {
    suspend operator fun invoke(): Result<List<CuratorTask>> {
        return repository.getTasks()
    }
}
