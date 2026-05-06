package com.umschool.umtasktracker.domain.model

data class ManagerTask(
    val id: String,
    val title: String,
    val description: String,
    val report: String,
    val deadline: String,
    val created: String,
    val status: TaskStatus,
    val progress: Number,
    val completed: Number,
    val total: Number,
    val notCompleted: Number,
    val sampleCurators: List<String>,
    val on_time: Number?,
    val not_on_time: Number?
)

