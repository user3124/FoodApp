package com.example.foodapp.core.domain.model

data class Meal(
    val id: Long = 0,
    val type: MealType,
    val timestamp: Long,
    val items: List<FoodItem>
) {
    val totalCalories: Int get() = items.sumOf { it.calories }
    val totalProtein: Double get() = items.sumOf { it.protein }
    val totalFat: Double get() = items.sumOf { it.fat }
    val totalCarbs: Double get() = items.sumOf { it.carbs }
}