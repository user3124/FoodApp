package com.example.foodapp.core.domain.repository

import com.example.foodapp.core.domain.model.FoodItem

interface AiRepository {
    /**
     * Разбирает текст вроде "сыр 100 грамм" на продукты с КБЖУ.
     * Если ничего не распознано, возвращает пустой список.
     */
    suspend fun recognizeFood(text: String): List<FoodItem>
}