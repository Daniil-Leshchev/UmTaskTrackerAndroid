package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// POST /api/register/ — запрос на регистрацию
// Поля соответствуют бэкенду: name (объединённое), role_id, subject_id, department_id
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("role_id") val roleId: Int,
    @SerialName("subject_id") val subjectId: Int,
    @SerialName("department_id") val departmentId: Int
)
