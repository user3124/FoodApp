package com.example.foodapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MealDao {

    @Transaction
    @Query(
        "SELECT * FROM meals " +
                "WHERE timestamp >= :start AND timestamp < :end " +
                "ORDER BY timestamp DESC"
    )
    abstract fun observeMeals(start: Long, end: Long): Flow<List<MealWithItems>>

    @Insert
    abstract suspend fun insertMeal(meal: MealEntity): Long

    @Insert
    abstract suspend fun insertItems(items: List<FoodItemEntity>)

    /** Приём пищи и его ингредиенты сохраняются атомарно. */
    @Transaction
    open suspend fun insertMealWithItems(meal: MealEntity, items: List<FoodItemEntity>) {
        val mealId = insertMeal(meal)
        insertItems(items.map { it.copy(mealId = mealId) })
    }

    @Query("SELECT COUNT(*) FROM meals WHERE timestamp >= :start AND timestamp < :end")
    abstract suspend fun countMeals(start: Long, end: Long): Int
}