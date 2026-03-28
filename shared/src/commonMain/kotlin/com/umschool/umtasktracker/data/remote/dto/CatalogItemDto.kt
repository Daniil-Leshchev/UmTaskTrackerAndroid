package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// GET /api/catalogs/roles/ → {"id_role": 1, "role": "Стандарт"}
@Serializable
data class RoleDto(
    @SerialName("id_role") val id: Int,
    @SerialName("role") val name: String
)

// GET /api/catalogs/subjects/ → {"id_subject": 1, "subject": "Информатика"}
@Serializable
data class SubjectDto(
    @SerialName("id_subject") val id: Int,
    @SerialName("subject") val name: String
)

// GET /api/catalogs/departments/ → {"id_department": 1, "department": "ЕГЭ"}
@Serializable
data class DepartmentDto(
    @SerialName("id_department") val id: Int,
    @SerialName("department") val name: String
)
