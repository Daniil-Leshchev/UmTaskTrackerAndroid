package com.umschool.umtasktracker.domain.model

data class CuratorTask(
    val id: String,
    val title: String,
    val description: String,
    val reportTemplate: String,
    val deadline: String,
    val created: String,
    val status: TaskStatus,
    val isCompleted: Boolean,
    val hasReport: Boolean,
    val reportUrl: String?
)

enum class TaskStatus(val label: String) {
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнено"),
    COMPLETED_LATE("Выполнено не в срок"),
    OVERDUE("Просрочено");

    companion object {
        fun fromString(value: String): TaskStatus = when (value.lowercase()) {
            "in_progress" -> IN_PROGRESS
            "completed" -> COMPLETED
            "completed_late" -> COMPLETED_LATE
            "overdue" -> OVERDUE
            else -> IN_PROGRESS
        }
    }
}
