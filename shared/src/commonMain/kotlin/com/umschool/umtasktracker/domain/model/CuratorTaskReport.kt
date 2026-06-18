package com.umschool.umtasktracker.domain.model

data class CuratorTaskReport(
    val reportText: String?,
    val fileUrls: List<String>,
    val isSubmitted: Boolean,
    val submittedAt: String?,
)
