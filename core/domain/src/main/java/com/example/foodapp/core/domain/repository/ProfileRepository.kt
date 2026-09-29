package com.example.foodapp.core.domain.repository

import com.example.foodapp.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    /** null, пока пользователь ещё ничего не вводил. */
    fun observeProfile(): Flow<UserProfile?>

    suspend fun saveProfile(profile: UserProfile)
}