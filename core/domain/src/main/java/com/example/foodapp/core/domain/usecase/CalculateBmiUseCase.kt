package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.model.Bmi
import com.example.foodapp.core.domain.model.BmiCategory
import javax.inject.Inject
import kotlin.math.round

class CalculateBmiUseCase @Inject constructor() {
    /** Возвращает null при некорректных данных (нулевой рост или вес). */
    operator fun invoke(heightCm: Int, weightKg: Double): Bmi? {
        if (heightCm <= 0 || weightKg <= 0.0) return null

        val heightM = heightCm / 100.0
        val value = round(weightKg / (heightM * heightM) * 10) / 10

        val category = when {
            value < 18.5 -> BmiCategory.UNDERWEIGHT
            value < 25.0 -> BmiCategory.NORMAL
            value < 30.0 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }
        return Bmi(value, category)
    }
}