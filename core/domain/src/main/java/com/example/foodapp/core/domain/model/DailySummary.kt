package com.example.foodapp.core.domain.model

data class DailySummary(
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val calorieGoal: Int = DEFAULT_CALORIE_GOAL
) {
    /** Доля выполненной нормы от 0f до 1f (для шкалы прогресса). */
    val progress: Float
        get() = if (calorieGoal <= 0) 0f
        else (calories.toFloat() / calorieGoal).coerceIn(0f, 1f)

    companion object {
        const val DEFAULT_CALORIE_GOAL = 2000
    }
}