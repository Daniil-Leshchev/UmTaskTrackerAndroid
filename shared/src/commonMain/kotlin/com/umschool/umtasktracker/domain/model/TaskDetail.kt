package com.umschool.umtasktracker.domain.model

data class TaskDetail(
    val email: String,
    val name: String,
    val role: String,
    val status: TaskStatus,
    val completedAt: String?,
    val reportUrl: String?,
    val reportText: String?
)