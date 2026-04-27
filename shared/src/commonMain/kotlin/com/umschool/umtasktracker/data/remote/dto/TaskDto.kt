package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("report_template")val reportTemplate: String,
    val deadline: String,
    val created: String,
    val status: String,
    @SerialName("completed_at") val completedAt: Boolean? = null,
    @SerialName("report_text") val reportText: Boolean? = null,
    @SerialName("report_url") val reportUrl: String? = null
)