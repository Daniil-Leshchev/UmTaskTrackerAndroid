package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipientDto(
    @SerialName("email") val email: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("role") val role: String = "",
    @SerialName("role_id") val roleId: Int = 0,
    @SerialName("department") val department: String = "",
    @SerialName("subject") val subject: String = ""
)
