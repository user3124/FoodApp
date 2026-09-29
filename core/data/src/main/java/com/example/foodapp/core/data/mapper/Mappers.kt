package com.example.foodapp.core.data.mapper

import com.example.foodapp.core.data.ai.AiFoodDto
import com.example.foodapp.core.data.local.FoodItemEntity
import com.example.foodapp.core.data.local.MealEntity
import com.example.foodapp.core.data.local.MealWithItems
import com.example.foodapp.core.data.local.ProfileEntity
import com.example.foodapp.core.domain.model.FoodItem
import com.example.foodapp.core.domain.model.Meal
import com.example.foodapp.core.domain.model.MealType
import com.example.foodapp.core.domain.model.UserProfile

// ---- Entity -> Domain ----

internal fun FoodItemEntity.toDomain() = FoodItem(
    id = id,
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs
)

internal fun MealWithItems.toDomain() = Meal(
    id = meal.id,
    type = MealType.entries.firstOrNull { it.name == meal.type } ?: MealType.SNACK,
    timestamp = meal.timestamp,
    items = items.map { it.toDomain() }
)

internal fun ProfileEntity.toDomain() = UserProfile(
    heightCm = heightCm,
    weightKg = weightKg
)

// ---- Domain -> Entity ----

internal fun Meal.toEntity() = MealEntity(
    id = id,
    type = type.name,
    timestamp = timestamp
)

internal fun FoodItem.toEntity(mealId: Long) = FoodItemEntity(
    id = id,
    mealId = mealId,
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs
)

internal fun UserProfile.toEntity() = ProfileEntity(
    heightCm = heightCm,
    weightKg = weightKg
)

// ---- DTO (ответ «нейросети») -> Domain ----

internal fun AiFoodDto.toDomain() = FoodItem(
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs
)