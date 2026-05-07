package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentPolicyDto(
    @SerialName("can_choose_department") val canChooseDepartment: Boolean = false,
    @SerialName("available_role_ids") val availableRoleIds: List<Int> = emptyList(),
    @SerialName("subject_id") val subjectId: Int = 0
)
