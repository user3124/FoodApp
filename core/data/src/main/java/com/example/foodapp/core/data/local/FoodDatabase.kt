package com.example.foodapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MealEntity::class, FoodItemEntity::class, ProfileEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun profileDao(): ProfileDao
}