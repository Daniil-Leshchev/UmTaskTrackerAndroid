package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    @SerialName("id_role") val id: Int,
    @SerialName("role") val name: String
)

@Serializable
data class SubjectDto(
    @SerialName("id_subject") val id: Int,
    @SerialName("subject") val name: String
)

@Serializable
data class DepartmentDto(
    @SerialName("id_department") val id: Int,
    @SerialName("department") val name: String
)
