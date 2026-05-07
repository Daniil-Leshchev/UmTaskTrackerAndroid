package com.umschool.umtasktracker.domain.model

data class AssignmentPolicy(
    val canChooseDepartment: Boolean,
    val availableRoleIds: List<Int>,
    val subjectId: Int
)
