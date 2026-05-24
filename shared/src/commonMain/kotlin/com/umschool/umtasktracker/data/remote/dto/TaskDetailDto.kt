package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDetailDto(
    val email: String,
    val name: String,
    val role: String,
    val status: String,
    val completedAt: String? = null,
    val reportUrl: String? = null,
    val reportText: String? = null
)