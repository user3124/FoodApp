package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.repository.MealRepository
import com.example.foodapp.core.domain.util.dayRangeOf
import javax.inject.Inject

class HasMealsTodayUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(now: Long = System.currentTimeMillis()): Boolean {
        val range = dayRangeOf(now)
        return mealRepository.countMeals(range.startMillis, range.endMillis) > 0
    }
}