package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CuratorTaskReportDto(
    @SerialName("report_text") val reportText: String? = null,
    @SerialName("report_url") val reportUrl: JsonElement? = null,
    @SerialName("timestamp_end") val timestampEnd: String? = null,
)
