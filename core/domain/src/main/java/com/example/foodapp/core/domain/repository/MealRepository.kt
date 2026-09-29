package com.example.foodapp.core.domain.repository

import com.example.foodapp.core.domain.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    /** Приёмы пищи в интервале [startMillis, endMillis), реактивно. */
    fun observeMeals(startMillis: Long, endMillis: Long): Flow<List<Meal>>

    suspend fun addMeal(meal: Meal)

    /** Нужен для NutritionReminderWorker. */
    suspend fun countMeals(startMillis: Long, endMillis: Long): Int
}