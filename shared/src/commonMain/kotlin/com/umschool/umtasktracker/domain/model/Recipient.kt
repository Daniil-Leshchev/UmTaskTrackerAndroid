package com.umschool.umtasktracker.domain.model

data class Recipient(
    val email: String,
    val name: String,
    val role: String,
    val roleId: Int,
    val department: String,
    val subject: String
)
