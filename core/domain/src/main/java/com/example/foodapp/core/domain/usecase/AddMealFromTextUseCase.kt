package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.model.AddMealResult
import com.example.foodapp.core.domain.model.Meal
import com.example.foodapp.core.domain.model.MealType
import com.example.foodapp.core.domain.repository.AiRepository
import com.example.foodapp.core.domain.repository.MealRepository
import javax.inject.Inject

class AddMealFromTextUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        text: String,
        now: Long = System.currentTimeMillis()
    ): AddMealResult {
        val query = text.trim()
        if (query.isEmpty()) return AddMealResult.EmptyInput

        val items = aiRepository.recognizeFood(query)
        if (items.isEmpty()) return AddMealResult.NotRecognized

        val meal = Meal(
            type = MealType.fromTimestamp(now),
            timestamp = now,
            items = items
        )
        mealRepository.addMeal(meal)
        return AddMealResult.Success(meal)
    }
}