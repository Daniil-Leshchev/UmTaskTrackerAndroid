package com.umschool.umtasktracker.presentation.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime


 object DateFormatter {

    private val moscow: TimeZone = TimeZone.of("Europe/Moscow")

    private val displayFormat = LocalDateTime.Format {
        dayOfMonth(Padding.ZERO); char('.')
        monthNumber(Padding.ZERO); char('.')
        year()
        chars(", ")
        hour(Padding.ZERO); char(':')
        minute(Padding.ZERO)
    }

    fun formatMoscow(iso: String): String? = runCatching {
        Instant.parse(iso).toLocalDateTime(moscow).format(displayFormat)
    }.getOrNull()
}
