package com.example.foodapp.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.foodapp.core.data.ai.AiRepositoryImpl
import com.example.foodapp.core.data.local.FoodDatabase
import com.example.foodapp.core.data.local.MealDao
import com.example.foodapp.core.data.local.ProfileDao
import com.example.foodapp.core.data.repository.MealRepositoryImpl
import com.example.foodapp.core.data.repository.ProfileRepositoryImpl
import com.example.foodapp.core.domain.repository.AiRepository
import com.example.foodapp.core.domain.repository.MealRepository
import com.example.foodapp.core.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): FoodDatabase =
            Room.databaseBuilder(context, FoodDatabase::class.java, "foodapp.db").build()

        @Provides
        fun provideMealDao(db: FoodDatabase): MealDao = db.mealDao()

        @Provides
        fun provideProfileDao(db: FoodDatabase): ProfileDao = db.profileDao()
    }
}