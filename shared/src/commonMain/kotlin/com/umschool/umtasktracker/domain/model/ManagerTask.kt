package com.umschool.umtasktracker.domain.model

data class ManagerTask(
    val id: String,
    val title: String,
    val description: String,
    val report: String,
    val deadline: String,
    val created: String,
    val status: ManagerTaskStatus,
    val progress: Number,
    val completed: Number,
    val total: Number,
    val notCompleted: Number,
    val sampleCurators: List<String>,
    val on_time: Number?,
    val not_on_time: Number?
)

enum class ManagerTaskStatus(val label: String) {
    NOT_STARTED("Не начато"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено");

    companion object {
        fun fromString(value: String): ManagerTaskStatus = when (value.lowercase()) {
            "не начато" -> NOT_STARTED
            "в процессе" -> IN_PROGRESS
            "завершено" -> COMPLETED
            else -> NOT_STARTED
        }
    }
}
