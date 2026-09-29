package com.example.foodapp.core.domain.model

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val grams: Int,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)