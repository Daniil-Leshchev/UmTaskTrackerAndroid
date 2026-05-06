package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerTaskDto(
    val id: String,
    val title: String,
    val description: String,
    val report: String,
    val deadline: String,
    val created: String,
    val status: String,
    val progress: Double,
    val completed: Int,
    val total: Int,
    val notCompleted: Int,
    val sampleCurators: List<String>,
    @SerialName("on_time") val onTime: Int? = null,
    @SerialName("not_on_time") val notOnTime: Int? = null
)

data class FetchTasksParams(
    val scope: String = "all",
    val subjectId: String? = null,
    val departmentId: String? = null,
    val status: String? = null,
    val query: String? = null
)