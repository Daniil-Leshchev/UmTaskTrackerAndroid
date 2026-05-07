package com.umschool.umtasktracker.domain.model

sealed class CreateTaskParams {

    abstract val name: String
    abstract val description: String
    abstract val deadlineIso: String
    abstract val reportFormat: String

    data class Individual(
        override val name: String,
        override val description: String,
        override val deadlineIso: String,
        override val reportFormat: String,
        val emails: List<String>
    ) : CreateTaskParams()

    data class Group(
        override val name: String,
        override val description: String,
        override val deadlineIso: String,
        override val reportFormat: String,
        val subjectId: Int,
        val departmentIds: List<Int>,
        val roleIds: List<Int>
    ) : CreateTaskParams()
}
