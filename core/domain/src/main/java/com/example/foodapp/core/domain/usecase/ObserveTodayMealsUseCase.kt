package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.model.Meal
import com.example.foodapp.core.domain.repository.MealRepository
import com.example.foodapp.core.domain.util.dayRangeOf
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTodayMealsUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    operator fun invoke(now: Long = System.currentTimeMillis()): Flow<List<Meal>> {
        val range = dayRangeOf(now)
        return mealRepository.observeMeals(range.startMillis, range.endMillis)
    }
}