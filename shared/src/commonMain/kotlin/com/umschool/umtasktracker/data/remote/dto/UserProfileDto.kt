package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class UserProfileDto(
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val subject: String? = null,
    val department: String? = null,
    val role: String,
    @SerialName("is_admin") val isAdmin: Boolean,
    @SerialName("is_confirmed") val isApproved: Boolean
)
