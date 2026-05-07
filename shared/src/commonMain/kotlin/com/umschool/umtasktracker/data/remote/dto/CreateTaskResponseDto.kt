package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliverySummaryDto(
    @SerialName("total") val total: Int,
    @SerialName("sent") val sent: Int,
    @SerialName("partial") val partial: Int,
    @SerialName("failed") val failed: Int
)

@Serializable
data class DeliveryDto(
    @SerialName("ok") val ok: Boolean,
    @SerialName("bot_unavailable") val botUnavailable: Boolean = false,
    @SerialName("summary") val summary: DeliverySummaryDto,
    @SerialName("undelivered_names_all") val undeliveredNamesAll: List<String> = emptyList()
)

@Serializable
data class CreateTaskResponseDto(
    @SerialName("id_task") val idTask: String,
    @SerialName("assignment_ids") val assignmentIds: List<Int>? = null,
    @SerialName("delivery") val delivery: DeliveryDto
)
