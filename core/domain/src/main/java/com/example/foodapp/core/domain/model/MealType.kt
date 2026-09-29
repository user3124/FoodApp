package com.example.foodapp.core.domain.model

import java.time.Instant
import java.time.ZoneId

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK;

    companion object {
        fun fromTimestamp(
            millis: Long,
            zone: ZoneId = ZoneId.systemDefault()
        ): MealType {
            val hour = Instant.ofEpochMilli(millis).atZone(zone).hour
            return when (hour) {
                in 5..10 -> BREAKFAST
                in 11..15 -> LUNCH
                in 16..21 -> DINNER
                else -> SNACK
            }
        }
    }
}