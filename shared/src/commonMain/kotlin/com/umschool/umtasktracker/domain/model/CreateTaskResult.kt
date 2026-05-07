package com.umschool.umtasktracker.domain.model

data class CreateTaskResult(
    val taskId: String,
    val totalAssigned: Int,
    val failedCount: Int,
    val undeliveredNames: List<String>
)
