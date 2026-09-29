package com.example.foodapp.core.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithItems(
    @Embedded val meal: MealEntity,
    @Relation(parentColumn = "id", entityColumn = "mealId")
    val items: List<FoodItemEntity>
)