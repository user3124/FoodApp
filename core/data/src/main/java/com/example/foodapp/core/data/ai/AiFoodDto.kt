package com.example.foodapp.core.data.ai

data class AiFoodDto(
    val name: String,
    val grams: Int,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)