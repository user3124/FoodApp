package com.example.foodapp.core.data.repository

import com.example.foodapp.core.data.local.MealDao
import com.example.foodapp.core.data.mapper.toDomain
import com.example.foodapp.core.data.mapper.toEntity
import com.example.foodapp.core.domain.model.Meal
import com.example.foodapp.core.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {

    override fun observeMeals(startMillis: Long, endMillis: Long): Flow<List<Meal>> =
        mealDao.observeMeals(startMillis, endMillis)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun addMeal(meal: Meal) {
        mealDao.insertMealWithItems(
            meal = meal.toEntity(),
            items = meal.items.map { it.toEntity(mealId = 0) } // реальный id проставит DAO
        )
    }

    override suspend fun countMeals(startMillis: Long, endMillis: Long): Int =
        mealDao.countMeals(startMillis, endMillis)
}