package com.umschool.umtasktracker.domain.usecase

import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.domain.repository.ManagerRepository

class GetRecipientsUseCase(
    private val repository: ManagerRepository
) {
    suspend operator fun invoke(): Result<List<Recipient>> =
        repository.getRecipients()
}
