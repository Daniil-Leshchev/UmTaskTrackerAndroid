package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.CuratorTaskReport
import com.umschool.umtasktracker.domain.repository.CuratorRepository

class GetCuratorReportUseCase(
    private val repository: CuratorRepository,
) {
    suspend operator fun invoke(taskId: String, email: String): Result<CuratorTaskReport> =
        repository.getReport(taskId, email)
}
