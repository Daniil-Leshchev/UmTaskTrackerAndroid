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
    OVERDUE("Просрочено"),
    NOT_STARTED("Не начато");

    companion object {
        fun fromString(value: String): TaskStatus = when (value.lowercase()) {
            "в работе" -> IN_PROGRESS
            "выполнено" -> COMPLETED
            "просрочено" -> OVERDUE
            "не начато" -> NOT_STARTED
            else -> NOT_STARTED
        }
    }
}
