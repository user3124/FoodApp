package com.example.foodapp.core.domain.util

import java.time.Instant
import java.time.ZoneId

/** Полуоткрытый интервал [startMillis, endMillis). */
data class DayRange(val startMillis: Long, val endMillis: Long)

fun dayRangeOf(
    timestamp: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault()
): DayRange {
    val date = Instant.ofEpochMilli(timestamp).atZone(zone).toLocalDate()
    val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
    val end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    return DayRange(start, end)
}