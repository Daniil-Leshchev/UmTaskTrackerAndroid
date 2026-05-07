package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskIndividualDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("deadline") val deadline: String,
    @SerialName("report") val report: String,
    @SerialName("emails") val emails: List<String>
)

@Serializable
data class CreateTaskGroupDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("deadline") val deadline: String,
    @SerialName("report") val report: String,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("department_ids") val departmentIds: List<Int>,
    @SerialName("role_ids") val roleIds: List<Int>
)
