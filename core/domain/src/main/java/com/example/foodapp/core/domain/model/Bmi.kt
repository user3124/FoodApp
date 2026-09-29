package com.example.foodapp.core.domain.model

enum class BmiCategory { UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE }

data class Bmi(
    val value: Double,
    val category: BmiCategory
)