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

    private val isoLocalFormat = LocalDateTime.Format {
        year(); char('-')
        monthNumber(Padding.ZERO); char('-')
        dayOfMonth(Padding.ZERO); char('T')
        hour(Padding.ZERO); char(':')
        minute(Padding.ZERO); char(':')
        second(Padding.ZERO)
    }

    fun formatMoscow(iso: String): String? = runCatching {
        Instant.parse(iso).toLocalDateTime(moscow).format(displayFormat)
    }.getOrNull()

    fun epochMillisToIso(millis: Long): String {
        val moscowDt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(moscow)
        return moscowDt.format(isoLocalFormat) + "+03:00"
    }

    fun composeMoscowIso(dateUtcMillis: Long, hour: Int, minute: Int): String {
        val date = Instant.fromEpochMilliseconds(dateUtcMillis).toLocalDateTime(TimeZone.UTC).date
        val ldt = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, minute, 0)
        return ldt.format(isoLocalFormat) + "+03:00"
    }

    fun moscowDisplayFromComponents(dateUtcMillis: Long, hour: Int, minute: Int): String {
        val iso = composeMoscowIso(dateUtcMillis, hour, minute)
        return formatMoscow(iso) ?: ""
    }
}
