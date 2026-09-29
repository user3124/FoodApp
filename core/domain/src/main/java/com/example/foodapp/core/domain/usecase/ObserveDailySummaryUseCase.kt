package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.model.DailySummary
import com.example.foodapp.core.domain.repository.MealRepository
import com.example.foodapp.core.domain.util.dayRangeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDailySummaryUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    operator fun invoke(now: Long = System.currentTimeMillis()): Flow<DailySummary> {
        val range = dayRangeOf(now)
        return mealRepository.observeMeals(range.startMillis, range.endMillis)
            .map { meals ->
                DailySummary(
                    calories = meals.sumOf { it.totalCalories },
                    protein = meals.sumOf { it.totalProtein },
                    fat = meals.sumOf { it.totalFat },
                    carbs = meals.sumOf { it.totalCarbs }
                )
            }
    }
}