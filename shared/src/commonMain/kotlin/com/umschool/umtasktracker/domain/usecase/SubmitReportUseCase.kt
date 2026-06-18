package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.SelectedFile
import com.umschool.umtasktracker.domain.repository.CuratorRepository

class SubmitReportUseCase(
    private val repository: CuratorRepository,
) {
    suspend operator fun invoke(
        taskId: String,
        reportText: String,
        files: List<SelectedFile>,
    ): Result<Unit> {
        if (reportText.isBlank() && files.isEmpty()) {
            return Result.failure(IllegalArgumentException("Заполните текст отчёта или прикрепите файл"))
        }
        return repository.submitReport(taskId, reportText.trim(), files)
    }
}
