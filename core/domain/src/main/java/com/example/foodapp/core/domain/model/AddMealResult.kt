package com.example.foodapp.core.domain.model

sealed interface AddMealResult {
    data class Success(val meal: Meal) : AddMealResult
    data object EmptyInput : AddMealResult
    data object NotRecognized : AddMealResult
}